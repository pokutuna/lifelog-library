package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Device(id: Pk[Int], address: String, deviceType: String, nomadic: String) {
  def this(address: String, deviceType: String, nomadic: String) = {
    this(NotAssigned, address, deviceType, nomadic)
  }
}

object Device {

  val tableName = "devices"

  val simple = {
    get[Pk[Int]]("id") ~/
    get[String]("address") ~/
    get[String]("device_type") ~/
    get[String]("nomadic") ^^ {
      case id~address~deviceType~nomadic => Device(id, address, deviceType, nomadic)
    }
  }

  def find(device: Device)(implicit connection: Connection): Option[Device] = {
    device.id match {
      case Id(_)       => findWithId(device)
      case NotAssigned => findIgnoreId(device)
    }
  }

  def findWithId(device: Device)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where id = {id} and address = {address} and device_type = {deviceType} and nomadic = {nomadic} limit 1"
    ).on(
      'id -> device.id, 'address -> device.address, 'deviceType -> device.deviceType,
      'nomadic -> device.nomadic
    ).as(simple ?)
  }

  def findIgnoreId(device: Device)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where address = {address} and device_type = {deviceType} and nomadic = {nomadic} limit 1"
    ).on(
      'address -> device.address, 'deviceType -> device.deviceType, 'nomadic -> device.nomadic
    ).as(simple ?)
  }

  def findById(id: Int)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where id = {id} limit 1"
    ).on('id -> id).as(simple ?)
  }

  def findByAddress(address: String)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where address = {address} limit 1"
    ).on('address -> address).as(simple ?)
  }

  def insertAsNeeded(device: Device)(implicit connection: Connection): Int = {
    find(device) match {
      case Some(d) => d.id.get
      case None    => insert(device)
    }
  }

  private def insert(device: Device)(implicit connection: Connection) = {
    SQL(
      "insert into " + tableName + "(address, device_type, nomadic) values({address}, {deviceType}, {nomadic})"
    ).on(
      'address -> device.address, 'deviceType -> device.deviceType, 'nomadic -> device.nomadic
    ).executeUpdate()
    SQL("select last_insert_rowid();").as(get[Int]("last_insert_rowid()"))
  }

  def updateNomadic(device: Device)(implicit connection: Connection) = {
    SQL(
      "update " + tableName + " set nomadic = {nomadic} where id = {id} and address = {address} and deviceType = {deviceType}"
    ).on(
      'nomadic -> device.nomadic, 'id -> device.id, 'address -> device.address,
      'deviceType -> device.deviceType
    ).executeUpdate()
  }

}
