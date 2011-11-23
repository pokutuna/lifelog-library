package com.pokutuna.lifelog.db.model

object SensingModel {

  case class WifiDetected(address: String, dateTime: String, strength: Int) extends DetectedRecord
}
