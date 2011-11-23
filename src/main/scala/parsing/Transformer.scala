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
    def toDeviceRecord: DeviceRecord = BtDevice(address, deviceName)
  }

  trait ToWifiDevice extends ToDeviceRecord {
    self: HasAddress with HasDeviceName =>
    def toDeviceRecord: DeviceRecord = WifiDevice(address, deviceName)
  }

  trait ToDetectRecord {
    def toDetectRecord: DetectedRecord
  }

  trait ToBtDetected extends ToDetectRecord {
    self: HasAddress with HasDate =>
    def toDetectRecord: DetectedRecord = BtDetected(address, TimeUtil.format(dateTime), 0)
  }

  trait ToWifiDetected extends ToDetectRecord with HasSignal {
    self: HasAddress with HasDate =>
    def toDetectRecord: DetectedRecord = WifiDetected(address, TimeUtil.format(dateTime), signal, 0)
  }
}
