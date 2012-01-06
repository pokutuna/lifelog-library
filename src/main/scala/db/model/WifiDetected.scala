package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class WifiDetected(address: String, dateTime: String, strength: Int, fileId: Int) extends DetectedRecord

object WifiDetected extends DetectedRecordQuery[WifiDetected]{

  val tableName = "wifi_detected"

  val simple = {
    get[String](tableName + ".address") ~ get[String](tableName + ".date_time") ~
    get[Int](tableName + ".strength") ~ get[Int](tableName + ".file_id") map {
      case address~dateTime~strength~fileId =>
        WifiDetected(address, dateTime, strength, fileId)
    }
  }

  def insert(wifiDetected: WifiDetected)(implicit connection: Connection): WifiDetected = {
    SQL(
      "insert into " + tableName + " values({address}, {dateTime}, {strength}, {fileId})"
    ).on(
      'address -> wifiDetected.address, 'dateTime -> wifiDetected.dateTime,
      'strength -> wifiDetected.strength, 'fileId -> wifiDetected.fileId
    ).executeUpdate()
    return wifiDetected
  }

}
