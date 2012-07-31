package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Device(id: Pk[Int], address: String, deviceType: String) {
  def this(address: String, deviceType: String) = {
    this(NotAssigned, address, deviceType)
  }
}

object Device {

  val tableName = "devices"

  val simple = {
    get[Pk[Int]]("id") ~ get[String]("address") ~ get[String]("device_type") map {
      case id~address~deviceType => Device(id, address, deviceType)
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
      "select * from " + tableName + " where id = {id} and address = {address} and device_type = {deviceType} limit 1"
    ).on(
      'id -> device.id, 'address -> device.address, 'deviceType -> device.deviceType
    ).as(simple.singleOpt)
  }

  def findIgnoreId(device: Device)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where address = {address} and device_type = {deviceType} limit 1"
    ).on(
      'address -> device.address, 'deviceType -> device.deviceType
    ).as(simple.singleOpt)
  }

  def findById(id: Int)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where id = {id} limit 1"
    ).on('id -> id).as(simple.singleOpt)
  }

  def findByAddress(address: String)(implicit connection: Connection): Option[Device] = {
    SQL(
      "select * from " + tableName + " where address = {address} limit 1"
    ).on('address -> address).as(simple.singleOpt)
  }

  def insertAsNeeded(device: Device)(implicit connection: Connection): Int = {
    find(device) match {
      case Some(d) => d.id.get
      case None    => insert(device)
    }
  }

  private def insert(device: Device)(implicit connection: Connection): Int = {
    SQL(
      "insert into " + tableName + "(address, device_type) values({address}, {deviceType})"
    ).on(
      'address -> device.address, 'deviceType -> device.deviceType
    ).executeUpdate()
    SQL("select last_insert_rowid();").as(scalar[Int].single)
  }

}
