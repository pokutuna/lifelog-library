package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import scala.collection.JavaConverters._

class SensingDBForJava(path: String) {
  val dao = new SensingDB(path)

  def detectedIn(from: String, to: String): java.util.List[DetectedRecord] = dao.detectedIn(from, to).asJava
  def btDetectedIn(from: String, to:String): java.util.List[BtDetected] = dao.btDetectedIn(from, to).asJava
  def wifiDetectedIn(from: String, to:String): java.util.List[WifiDetected] = dao.wifiDetectedIn(from, to).asJava

  def countDetection(address: String): Int = dao.countDetection(address)
  def latestDateTime: String = dao.latestDateTime
  def addressToName(address: String): String =
    dao.addressToName(address) match {
      case Some(name) => name
      case None => ""
    }

  def isBluetooth(address: String): Boolean = dao.isBluetooth(address)
  def isWifi(address: String): Boolean = dao.isWifi(address)

  def insertBtDevice(address: String, name: String) = dao.insertBtDevice(BtDevice(address, name))
  def insertBtDevice(device: BtDevice) = dao.insertBtDevice(device)
  def insertWifiDevice(address: String, name: String) = dao.insertWifiDevice(WifiDevice(address, name))
  def insertWifiDevice(device: WifiDevice) = dao.insertWifiDevice(device)

  def insertBtDetected(address: String, dateTime: String) = dao.insertBtDetected(BtDetected(address, dateTime, 0))
  def insertBtDetected(detection: BtDetected) = dao.insertBtDetected(detection)
  def insertWifiDetected(address: String, dateTime: String, strength: Int) = dao.insertWifiDetected(WifiDetected(address, dateTime, strength, 0))
  def insertWifiDetected(detection: WifiDetected) = dao.insertWifiDetected(detection)

}
