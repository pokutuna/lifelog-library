package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import java.io.File
import anorm._
import anorm.SqlParser._

class SensingDB(path: String) extends Database(path) with Schema {

  val schemaFile = new File("db/sensing.sql")

  def detectedIn(start: String, end:String): Seq[DetectedRecord] = {
    (btDetectedIn(start, end) ++ wifiDetectedIn(start, end)).sortBy(_.dateTime)
  }

  def detectedIn(start: String, end: String, offset: Int, limit: Int) = {
    (btDetectedIn(start, end, offset, limit) ++
     wifiDetectedIn(start, end, offset, limit)).sortBy(_.dateTime)
  }

  def btDetectedIn(start: String, end: String): Seq[BtDetected] = {
    withConnection { implicit connection =>
      BtDetected.findByDateTime(start, end)
    }
  }

  def btDetectedIn(start: String, end: String, offset: Int, limit: Int) = {
    withConnection { implicit connection =>
      BtDetected.findByDateTime(start, end, offset, limit)
    }
  }

  def wifiDetectedIn(start: String, end: String): Seq[WifiDetected] = {
    withConnection { implicit connection =>
      WifiDetected.findByDateTime(start, end)
    }
  }

  def wifiDetectedIn(start: String, end: String, offset: Int, limit: Int) = {
    withConnection { implicit connection =>
      WifiDetected.findByDateTime(start, end, offset, limit)
    }
  }

  def addressToName(address: String): Option[String] = {
    btAddressToName(address).orElse(wifiAddressToName(address))
  }

  def btAddressToName(address: String): Option[String] = {
    withConnection { implicit connection =>
      BtDevice.findByAddress(address) match {
        case Some(device) => Some(device.name)
        case None         => None
      }
    }
  }

  def wifiAddressToName(address: String): Option[String] = {
    withConnection { implicit connection =>
      WifiDevice.findByAddress(address) match {
        case Some(device) => Some(device.name)
        case None         => None
      }
    }
  }

  def isBluetooth(address: String): Boolean = {
    withConnection { implicit connection =>
      BtDevice.findByAddress(address) match {
        case Some(_) => true
        case None    => false
      }
    }
  }

  def isWifi(address: String): Boolean = {
    withConnection { implicit connection =>
      WifiDevice.findByAddress(address) match {
        case Some(_) => true
        case None    => false
      }
    }
  }

  def insertBtDevice(btDevice: BtDevice) = {
    withConnection { implicit connection =>
      BtDevice.insertOrUpdate(btDevice)
    }
  }

  def insertBtDevice(btDevices: Seq[BtDevice]) = {
    withTransaction { implicit connection =>
      for (record <- btDevices) {
        BtDevice.insertOrUpdate(record)
      }
    }
  }

  def insertWifiDevice(wifiDevice: WifiDevice) = {
    withConnection { implicit connection =>
      WifiDevice.insertOrUpdate(wifiDevice)
    }
  }

  def insertWifiDevice(wifiDevices: Seq[WifiDevice]) = {
    withTransaction { implicit connection =>
      for (record <- wifiDevices) {
        WifiDevice.insertOrUpdate(record)
      }
    }
  }

  def insertBtDetected(btDetected: BtDetected) = {
    withConnection { implicit connection =>
      BtDetected.insertUnique(btDetected)
    }
  }

  def insertBtDetected(btDetecteds: Seq[BtDetected]) = {
    withTransaction { implicit connection =>
      for (record <- btDetecteds) {
        BtDetected.insertUnique(record)
      }
    }
  }

  def insertWifiDetected(wifiDetected: WifiDetected) = {
    withConnection { implicit connection =>
      WifiDetected.insertUnique(wifiDetected)
    }
  }

  def insertWifiDetected(wifiDetecteds: Seq[WifiDetected]) = {
    withTransaction { implicit connection =>
      for (record <- wifiDetecteds) {
        WifiDetected.insertUnique(record)
      }
    }
  }

  def countDetection(address: String): Int = {
    if (isBluetooth(address)) {
      withConnection(implicit connection => BtDetected.countAddress(address))
    } else if (isWifi(address)) {
      withConnection(implicit connection => WifiDetected.countAddress(address))
    } else {
      0
    }
  }

  def latestDateTime: String = {
    val latests = withConnection { implicit connection =>
      (BtDetected.latestDateTime, WifiDetected.latestDateTime)
    }
    if (latests._1 >= latests._2) latests._1 else latests._2
  }

}
