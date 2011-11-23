package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class BtDetected(address: String, dateTime: String, fileId: Int) extends DetectedRecord

object BtDetected extends DetectedRecordQuery[BtDetected] {

  val tableName = "bt_detected"

  val simple = {
    get[String](tableName + ".address") ~/
    get[String](tableName + ".date_time") ~/
    get[Int](tableName + ".file_id") ^^ {
      case address~dateTime~fileId => BtDetected(address, dateTime, fileId)
    }
  }

  def insert(btDetected: BtDetected)(implicit connection: Connection): BtDetected = {
    SQL(
      "insert into " + tableName + " values({address}, {dateTime}, {fileId})"
    ).on(
      'address -> btDetected.address, 'dateTime -> btDetected.dateTime,
      'fileId -> btDetected.fileId
    ).executeUpdate()
    return btDetected
  }

}
