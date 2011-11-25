package com.pokutuna.lifelog.parsing

import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.parsing.LogToken._
import com.pokutuna.lifelog.util.TimeUtil

object Transformer {

  trait ToDeviceRecord {
    def toDeviceRecord: DeviceRecord
  }

  trait ToBtDevice extends ToDeviceRecord {
    self: HasAddress with HasDeviceName =>
    override def toDeviceRecord: BtDevice = BtDevice(address, deviceName)
  }

  trait ToWifiDevice extends ToDeviceRecord {
    self: HasAddress with HasDeviceName =>
    override def toDeviceRecord: WifiDevice = WifiDevice(address, deviceName)
  }

  trait ToDetectRecord {
    def toDetectRecord(fileId: Int): DetectedRecord
  }

  trait ToBtDetected extends ToDetectRecord {
    self: HasAddress with HasDate =>

    override def toDetectRecord(fileId: Int): BtDetected = {
      BtDetected(address, TimeUtil.format(dateTime), fileId)
    }
  }

  trait ToWifiDetected extends ToDetectRecord with HasSignal {
    self: HasAddress with HasDate =>

    override def toDetectRecord(fileId: Int): WifiDetected = {
      WifiDetected(address, TimeUtil.format(dateTime), signal, fileId)
    }
  }
}
