package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class BtDevice(address: String, name: String) extends DeviceRecord

object BtDevice extends DeviceRecordQuery[BtDevice]{

  val tableName = "bt_devices"

  val simple = {
    get[String](tableName + ".address") ~/
    get[String](tableName + ".name") ^^ {
      case address~name => BtDevice(address, name)
    }
  }
}
