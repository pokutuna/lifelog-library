package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class BtDetected(address: String, dateTime: String, fileId: Int) extends DetectedRecord

object BtDetected {

  val tableName = "bt_detected"

  val simple = {
    get[String](tableName + ".address") ~/
    get[String](tableName + ".date_time") ~/
    get[Int](tableName + ".file_id") ^^ {
      case address~dateTime~fileId =>
        BtDetected(address, dateTime, fileId)
    }
  }

  def findByFileId(fileId: Int)(implicit connection: Connection): Seq[BtDetected] = {
    SQL(
      "select * from {table} where file_id = {fileId}"
    ).on(
      'table -> tableName, 'fileId -> fileId
    ).as(BtDetected.simple *)
  }

  def findByAddress(address: String)(implicit connection:Connection): Seq[BtDetected] = {
    SQL(
      "select * from {table} where address = {address} order by date_time"
    ).on(
      'table -> tableName, 'address -> address
    ).as(BtDetected.simple *)
  }

  def findByAddress(address: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[BtDetected] = {
    SQL(
      """
      select * from {table} where address = {address} order by date_time
       limit {limit} offset {offset}
      """
    ).on(
      'table -> tableName, 'address -> address, 'offset -> offset, 'limit -> limit
    ).as(BtDetected.simple *)
  }

  def findByDateTime(start: String, end: String)(implicit connection: Connection): Seq[BtDetected] = {
    SQL(
      """
      select * from {table} where {startTime} <= date_time and date_time < {endTime}
       order by date_time
      """
    ).on(
      'table -> tableName, 'startTime -> start, 'endTime -> end
    ).as(BtDetected.simple *)
  }

  def findByDateTime(start: String, end: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[BtDetected] = {
    SQL(
      """
      select * from {table} where {startTime} <= date_time and date_time < {endTime}
       order by date_time limit {limit} offset {offset}
      """
    ).on(
      'table -> tableName, 'startTime -> start, 'endTime -> end,
      'limit -> limit, 'offset -> offset
    ).as(BtDetected.simple *)
  }

  def deteleBtDetected(btDetected: BtDetected)(implicit connection: Connection) {
    SQL(
      """
      delete from {table} where address = {address} and date_time = {dateTime}
       and file_id = {fileId}
      """
    ).on(
      'table -> tableName, 'address -> btDetected.address,
      'dateTime -> btDetected.dateTime, 'fileId -> btDetected.fileId
    ).executeUpdate()
  }

  def insertBtDetected(btDetected: BtDetected)(implicit connection: Connection): BtDetected = {
    SQL(
      """
      insert into {table} values({address}, {dateTime}, {fileId})
      """
    ).on(
      'table -> tableName, 'address -> btDetected.address,
      'dateTime -> btDetected.dateTime, 'fileId -> btDetected.fileId
    ).executeUpdate()
    return btDetected
  }

}
