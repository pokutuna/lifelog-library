package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.parsing.Transformer._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.parsing.LogToken._
import com.pokutuna.lifelog.util.TimeUtil

class TransformerSpec extends SpecHelper {
  describe("Transformer") {

    val b = BtDetectLog(TimeUtil.parse("2011-06-06 06:49:46"), "pokutuna-MBA", "58:55:CA:FB:56:D2")
    val w = WifiDetectLog(TimeUtil.parse("2011-06-06 06:49:44"), "TUNACAN", "00:18:84:89:A9:74", -37)

    it("should convert DetectLog to DetectRecord") {
      b.toDetectRecord should be (BtDetected("58:55:CA:FB:56:D2", "2011-06-06 06:49:46", 0))
      w.toDetectRecord should be (WifiDetected("00:18:84:89:A9:74", "2011-06-06 06:49:44", -37, 0))
    }

    it("should convert DetectLog to DeviceRecord") {
      b.toDeviceRecord should be (BtDevice("58:55:CA:FB:56:D2", "pokutuna-MBA"))
      w.toDeviceRecord should be (WifiDevice("00:18:84:89:A9:74", "TUNACAN"))
    }

    it("should classify Bt and Wifi") {
      val l = List(b, w).map(_.toDeviceRecord)
      l should be (List(BtDevice("58:55:CA:FB:56:D2", "pokutuna-MBA"), WifiDevice("00:18:84:89:A9:74", "TUNACAN")))
    }
  }
}
