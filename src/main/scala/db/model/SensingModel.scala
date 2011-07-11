package com.pokutuna.lifelog.db.model

object SensingModel {

  //Device
  trait DeviceRecord {
    val address: String
    val name: String
  }

  case class BtDevice(address: String, name: String) extends DeviceRecord

  case class WifiDevice(address: String, name: String) extends DeviceRecord

  //Detected
  trait DetectedRecord {
    val address: String
    val dateTime: String
  }

  case class BtDetected(address: String, dateTime: String) extends DetectedRecord

  case class WifiDetected(address: String, dateTime: String, strength: Int) extends DetectedRecord
}
