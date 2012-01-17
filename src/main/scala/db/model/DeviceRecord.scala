package com.pokutuna.lifelog.db.model

trait DeviceRecord {
  val address: String
  val name: String

  def toDevice: Device = {
    val deviceType = this match {
      case b if b.isInstanceOf[BtDevice]   => "bt"
      case w if w.isInstanceOf[WifiDevice] => "wf"
      case _ => throw new RuntimeException(this.toString + " can't convert to tag")
    }
    new Device(address, deviceType)
  }
}
