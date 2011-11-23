package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class WifiDevice(address: String, name: String) extends DeviceRecord

object WifiDevice extends DeviceRecordQuery[WifiDevice] {

  val tableName = "wifi_devices"

  val simple = {
    get[String](tableName + ".address") ~/
    get[String](tableName + ".name") ^^ {
      case address~name => WifiDevice(address, name)
    }
  }
}
