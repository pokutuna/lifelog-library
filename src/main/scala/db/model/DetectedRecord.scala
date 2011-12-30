package com.pokutuna.lifelog.db.model

trait DetectedRecord {
  val address: String
  val dateTime: String
  val fileId: Int

  def toDevice: Device = {
    val deviceType = this match {
      case b if b.isInstanceOf[BtDetected]   => "bt"
      case w if w.isInstanceOf[WifiDetected] => "wf"
      case _ => throw new RuntimeException("can't conver to tag'")
    }
    new Device(address, deviceType, "")
  }

  def toTag(deviceId: Int, photoId: Int): Tag = {
    new Tag(deviceId, photoId)
  }
}
