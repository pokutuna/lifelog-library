package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import anorm._

class SensingDBSpec extends SpecHelper {

  val db = new SensingDB("src/test/resources/test_sensing.db")

  val devA = BtDevice("AddrA", "DevNameA")
  val devB = WifiDevice("AddrB", "DevNameB")
  val devC = BtDevice("AddrC", "DevNameC")
  val devD = WifiDevice("AddrD", "DevNameD")

  val recA1 = BtDetected("AddrA", "2011-07-07 00:01:00", 0)
  val recA2 = BtDetected("AddrA", "2011-07-08 00:02:00", 0)
  val recAOld = BtDetected("AddrA", "2000-07-07 00:03:00", 0)
  val recB1 = WifiDetected("AddrB", "2011-07-07 00:00:00", -10, 0)
  val recB2 = WifiDetected("AddrB", "2011-07-07 00:01:00", -10, 0)
  val recB3 = WifiDetected("AddrD", "2011-07-07 00:02:00", -10, 0)
  val recLatest = WifiDetected("AddrE", "2012-07-07 00:00:00", -10, 0)


  def cleanDB = {
    import scala.util.control.Exception._
    allCatch.opt(db.applySchema)
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

  describe("Device Detected In with Paging") {
    it("should get WifiRecord with Paging") {
      val records = db.wifiDetectedIn("2011-07-07 00:00:00", "2011-07-07 00:02:59", 1, 1).toList
      records should be (List(recB2))
    }
  }

  describe("Search Detected") {
    it("should get DetectedRecord with Glob search") {
      val records = db.searchDatePrefix("2011-07-07 ").toList
      records should be (List(recB1, recA1, recB2, recB3))
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
      db.latestDateTime should be ("2012-07-07 00:00:00")
    }
  }

  describe("Insersion") {
    it("should insert device") {
      db.insertBtDevice(BtDevice("AddrInsert", "DevNameInsert"))
      db.isBluetooth("AddrInsert") should be (true)
      db.addressToName("AddrInsert") should be (Some("DevNameInsert"))

      db.insertWifiDevice(WifiDevice("AddrInsert2", "DevNameInsert2"))
      db.isWifi("AddrInsert2") should be (true)
      db.addressToName("AddrInsert2") should be (Some("DevNameInsert2"))
    }

    it("should update device name") {
      db.insertBtDevice(BtDevice("AddrInsert", "DevNameInsert"))
      db.addressToName("AddrInsert") should be (Some("DevNameInsert"))
      db.insertBtDevice(BtDevice("AddrInsert", "DevNewName"))
      db.addressToName("AddrInsert") should be (Some("DevNewName"))
    }

    it("should insert detection") {
      db.insertBtDetected(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0))
      val record = db.btDetectedIn("2013-07-06 00:00:00", "2013-07-08 00:00:00").toList
      record should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0)))

      db.insertWifiDetected(WifiDetected("AddrInsert", "2013-07-08 00:00:00", -10, 0))
      val records = db.detectedIn("2013-07-06 00:00:00", "2013-07-09 00:00:00").toList
      records should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0), WifiDetected("AddrInsert", "2013-07-08 00:00:00", -10, 0)))
    }

    it("should not insert same detection informations") {
      db.insertBtDetected(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0))
      db.insertBtDetected(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0))
      val record = db.btDetectedIn("2013-07-06 00:00:00", "2013-07-08 00:00:00").toList
      record should be (List(BtDetected("AddrInsert", "2013-07-07 00:00:00", 0)))
    }
  }

  describe("SensingDB Migrating") {
    it("should read schema file") {
      val mydb = new SensingDB(":memory:")
      mydb.readSchema
    }

    it("should apply schema") {
      val mydb = new SensingDB("src/test/resources/test_sensing.db")
      mydb.applySchema
      mydb.withConnection { implicit conn =>
        SQL("insert into bt_detected values ({address}, {date_time}, {file_id});").on(
          'address -> "hoge", 'date_time -> "2011-11-15 15:16:00", 'file_id -> 1
        ).executeUpdate()
      }
    }
  }
}
