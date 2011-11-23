package com.pokutuna.lifelog.db.model

object SensingModel {

  case class WifiDevice(address: String, name: String) extends DeviceRecord

  case class WifiDetected(address: String, dateTime: String, strength: Int) extends DetectedRecord
}
