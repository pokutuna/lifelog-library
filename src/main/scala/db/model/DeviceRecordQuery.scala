package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

trait DeviceRecordQuery[T <: DeviceRecord] {

  val tableName: String
  val simple:RowParser[T]

  def find(device: T)(implicit connection: Connection): Option[T] = {
    SQL(
      "select * from " + tableName + " where address = {address} and name = {name}"
    ).on(
      'address -> device.address, 'name -> device.name
    ).as(simple.singleOpt)
  }

  def findByName(name: String)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where name = {name}"
    ).on('name -> name).as(simple *)
  }

  def findByAddress(address: String)(implicit connection: Connection): Option[T] = {
    SQL(
      "select * from " + tableName + " where address = {address}"
    ).on('address -> address).as(simple.singleOpt)
  }

  def delete(device: T)(implicit connection: Connection) = {
    SQL(
      "delete from " + tableName + " where address = {address} and name = {name}"
    ).on(
      'address -> device.address, 'name -> device.name
    ).executeUpdate()
  }

  def insertOrUpdate(device: T)(implicit connection: Connection) = {
    findByAddress(device.address) match {
      case Some(_) => updateName(device.address, device.name)
      case None    => insert(device)
    }
  }

  def updateName(address: String, name: String)(implicit connection: Connection) = {
    SQL(
      "update " + tableName + " set name = {name} where address = {address}"
    ).on('name -> name, 'address -> address).execute()
  }

  private def insert(device: T)(implicit connection: Connection): T = {
    SQL(
      "insert into " + tableName + " values({address}, {name})"
    ).on(
      'address -> device.address, 'name -> device.name
    ).executeUpdate()
    return device
  }

}
