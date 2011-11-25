package com.pokutuna.lifelog.db.model

trait DetectedRecord {
  val address: String
  val dateTime: String
  val fileId: Int

  def toTag(photoId: Int): Tag = {
    val deviceType = this match {
      case b if b.isInstanceOf[BtDetected]   => "bt"
      case w if w.isInstanceOf[WifiDetected] => "wf"
      case _ => throw new RuntimeException("can't conver to tag'")
    }
    new Tag(photoId, address, deviceType)
  }
}
