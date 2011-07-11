package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model.SensingModel._

class SensingDAOSpec extends SpecHelper {

  val db = new SensingDAO("jdbc:sqlite:src/test/resources/test_sensing.db")

  val devA = BtDevice("AddrA", "DevNameA")
  val devB = WifiDevice("AddrB", "DevNameB")
  val devC = BtDevice("AddrC", "DevNameC")
  val devD = WifiDevice("AddrD", "DevNameD")

  val recA1 = BtDetected("AddrA", "2011-07-07 00:01:00")
  val recA2 = BtDetected("AddrA", "2011-07-08 00:02:00")
  val recAOld = BtDetected("AddrA", "2000-07-07 00:03:00")
  val recB1 = WifiDetected("AddrB", "2011-07-07 00:00:00", -10)
  val recB2 = WifiDetected("AddrB", "2011-07-07 00:01:00", -10)
  val recB3 = WifiDetected("AddrD", "2011-07-07 00:02:00", -10)
  val recLatest = WifiDetected("AddrE", "2012-07-07 00:00:00", -10)


  def cleanDB = {
    import scala.util.control.Exception._
    allCatch.opt(db.dropAll)
    allCatch.opt(db.createAll)
  }

  def insertExamples = {
    db.insertBtDevice(devA)
    db.insertWifiDevice(devB)
    db.insertBtDevice(devC)
    db.insertWifiDevice(devD)

    db.insertBtDetected(recA1)
    db.insertBtDetected(recA2)
    db.insertBtDetected(recAOld)
    db.insertWifiDetected(recB1)
    db.insertWifiDetected(recB2)
    db.insertWifiDetected(recB3)
    db.insertWifiDetected(recLatest)
  }

  override def beforeEach = {
    cleanDB
    insertExamples
  }

  describe("Device Detected In") {
    it("should get DetectRecord") {
      val records = db.detectedIn("2011-07-07 00:00:00", "2011-07-07 00:02:59").toList
      records should be (List(recB1, recA1, recB2, recB3))
    }

    it("should get BtDetected") {
      val records = db.btDetectedIn("2011-07-07 00:00:00", "2011-07-07 00:02:59").toList
      records should be (List(recA1))
    }

    it("should get WifiDetected") {
      val records = db.wifiDetectedIn("2011-07-07 00:00:00", "2011-07-07 00:02:59").toList
      records should be (List(recB1, recB2, recB3))
    }

    it("should get sorted DetectRecords") {
      val records = db.btDetectedIn("2000-01-01 00:00:00", "2011-07-07 00:02:00").toList
      records should be (List(recAOld, recA1))
    }
  }

  describe("Methods for Address") {
    it("should get device name by its address") {
      db.addressToName("AddrA") should be (Some("DevNameA"))
      db.addressToName("AddrD") should be (Some("DevNameD"))
      db.addressToName("Undefined") should be (None)
    }

    it("should check device type Bluetooth or Wifi") {
      db.isBluetooth("AddrA") should be (true)
      db.isBluetooth("AddrB") should be (false)
      db.isBluetooth("AddrC") should be (true)
      db.isBluetooth("Undefined") should be (false)
      db.isWifi("AddrD") should be (true)
      db.isWifi("AddrA") should be (false)
      db.isWifi("Undefined") should be (false)
    }

    it("should count detection times") {
      db.countDetection("AddrA") should be === 3
      db.countDetection("AddrB") should be === 2
      db.countDetection("AddrD") should be === 1
      db.countDetection("Undefined") should be === 0
    }
  }

  describe("Other") {
    it("should get latest detection date") {
      db.latestDate should be ("2012-07-07 00:00:00")
    }
  }

  describe("Insersion") {
    it("should insert device") {
      db.insertBtDevice("AddrInsert", "DevNameInsert")
      db.isBluetooth("AddrInsert") should be (true)
      db.addressToName("AddrInsert") should be (Some("DevNameInsert"))

      db.insertWifiDevice(WifiDevice("AddrInsert2", "DevNameInsert2"))
      db.isWifi("AddrInsert2") should be (true)
      db.addressToName("AddrInsert2") should be (Some("DevNameInsert2"))
    }

    it("should update device name") {
      db.insertBtDevice("AddrInsert", "DevNameInsert")
      db.addressToName("AddrInsert") should be (Some("DevNameInsert"))
      db.insertBtDevice("AddrInsert", "DevNewName")
      db.addressToName("AddrInsert") should be (Some("DevNewName"))
    }

    it("should insert detection") {
      db.insertBtDetected("AddrInsert", "2013-07-07 00:00:00")
      val record = db.btDetectedIn("2013-07-06 00:00:00", "2013-07-08 00:00:00").toList
      record should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00")))

      db.insertWifiDetected(WifiDetected("AddrInsert", "2013-07-08 00:00:00", -10))
      val records = db.detectedIn("2013-07-06 00:00:00", "2013-07-09 00:00:00").toList
      records should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00"), WifiDetected("AddrInsert", "2013-07-08 00:00:00", -10)))
    }

    it("should not insert same detection informations") {
      db.insertBtDetected(BtDetected("AddrInsert", "2013-07-07 00:00:00"))
      db.insertBtDetected(BtDetected("AddrInsert", "2013-07-07 00:00:00"))
      val record = db.btDetectedIn("2013-07-06 00:00:00", "2013-07-08 00:00:00").toList
      record should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00")))
    }
  }
}










