package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.parsing._
import com.pokutuna.lifelog.parsing.LogParser._
import com.pokutuna.lifelog.parsing.LogToken._
import com.pokutuna.lifelog.util.TimeUtil
import java.io.FileReader
import java.util.Date

class LogParserSpec extends SpecHelper {

  describe("Detect element") {
    it("should parse bluetooth detect log") {
      val a = parseAll(btDetect, "2011/06/06 06:49:46	pokutuna-MBA	58:55:CA:FB:56:D2").get
      val b = BtDetectLog(TimeUtil.parse("2011-06-06 06:49:46"), "pokutuna-MBA", "58:55:CA:FB:56:D2")
      a should be (b)
    }

    it("should parse wifi detect log") {
      val a = parseAll(wifiDetect, "2011/06/06 06:49:44	TUNACAN	00:18:84:89:A9:74	-37").get
      val b = WifiDetectLog(TimeUtil.parse("2011-06-06 06:49:44"), "TUNACAN", "00:18:84:89:A9:74", -37)
      a should be (b)
    }

    it("should parse both detect log") {
      val a = parseAll(detectLog, "2011/06/06 06:49:44	TUNACAN	00:18:84:89:A9:74	-37").get
      a.getClass should be (classOf[WifiDetectLog])

      val b = parseAll(btDetect, "2011/06/06 06:49:46	pokutuna-MBA	58:55:CA:FB:56:D2").get
      b.getClass should be (classOf[BtDetectLog])
    }
  }

  describe("Device Name element") {
    it("should parse devicename") {
      parseAll(deviceName, "hogehogehoge").get should be ("hogehogehoge")
      parseAll(deviceName, "日本語日本語表").get should be ("日本語日本語表")
      parseAll(deviceName, "with space foo").get should be ("with space foo")
      parseAll(deviceName, "with space\t").successful should be (false)
    }
  }

  describe("Address element") {
    it("should parse address") {
      parseAll(address, "00:00:00:00:00:00").get should be ("00:00:00:00:00:00")
      parseAll(address, "00:00:00:00:00:00:00").successful should be (false)
      parseAll(address, "00:00:00:00:00").successful should be (false)
      parseAll(address, "ab:cd:ef:01:23:45").get should be ("AB:CD:EF:01:23:45")
    }

    it("should parse hexiadicimal numbers") {
      parseAll(hexadecimal, "a").get should be ("a")
      parseAll(hexadecimal, "0").get should be ("0")
      parseAll(hexadecimal, "F").get should be ("F")
      parseAll(hexadecimal, "g").successful should be (false)
    }
  }

  describe("Date element") {
    it("should parse date time") {
      val a = parseAll(dateTime, "2011/11/22 10:10:10").get
      val b = TimeUtil.parse("2011-11-22 10:10:10")
      a should be (b)
    }

    it("should parse date") {
      parseAll(date, "2011/1/10").get should be (2011,1,10)
      parseAll(date, "2011-1-10").get should be (2011,1,10)
      parseAll(date, "2011/01/01").get should be (2011,1,1)
    }

    it("should parse year") {
      parseAll(year, "1900").get should be === 1900
      parseAll(year, "2001").get should be === 2001
      parseAll(year, "abce").successful should be (false)
      parseAll(year, "1899").successful should be (false)
    }

    it("should parse month") {
      parseAll(month, "10").get should be === 10
      parseAll(month, "1").get should be === 1
      parseAll(month, "13").successful should be (false)
      parseAll(month, "ab").successful should be (false)      
      parseAll(month, "0").successful should be (false)
      parseAll(month, "09").get should be === 9
    }

    it("should parse day") {
      parseAll(day, "10").get should be === 10
      parseAll(day, "01").get should be === 1
      parseAll(day, "9").get should be === 9
    }

    it("should parse time") {
      parseAll(time, "23:59:59").get should be (23,59,59)
      parseAll(time, "00:00:00").get should be (0,0,0)
    }

    it("should parse hour") {
      parseAll(hours, "01").get should be === 1
      parseAll(hours, "12").get should be === 12
      parseAll(hours, "24").successful should be (false)
      parseAll(hours, "ab").successful should be (false)
    }

    it("should parse minutes & seconds") {
      parseAll(minutes, "00").get should be === 0
      parseAll(minutes, "59").get should be === 59
      parseAll(minutes, "60").successful should be (false)
      parseAll(minutes, "ab").successful should be (false)
    }

    it("should parse positive digit") {
      parseAll(positiveDigit, "1").get should be === "1"
      parseAll(positiveDigit, "0").successful should be (false)
    }

    it("should parse digit") {
      parseAll(digit, "0").get should be === "0"
      parseAll(digit, "9").get should be === "9"
      parseAll(digit, "a").successful should be (false)
      parseAll(digit, "01").successful should be (false)
    }
  }

  describe("Annotation element") {
    it("should parse each annotations") {
      parseAll(annotationLog, "[LOGGER_VERSION]0.0.3").get should be (LoggerVersion("0.0.3"))
      parseAll(annotationLog, "[LOGGER_BDA]70:71:bc:21:11:1e").get should be (LoggerBDA("70:71:BC:21:11:1E"))
      parseAll(annotationLog, "[WIFI_SCAN]2011/06/06 06:50:28").get should be (WifiScan(TimeUtil.parse("2011-06-06 06:50:28")))
      parseAll(annotationLog, "[BT_SCAN]2011/06/06 06:50:48").get should be (BtScan(TimeUtil.parse("2011-06-06 06:50:48")))
    }

    it("should parse version element") {
      parseAll(versionAnno, "[LOGGER_VERSION]0.0.3").get should be (LoggerVersion("0.0.3"))
    }

    it("should parse logger bda element") {
      val a =parseAll(loggerBdaAnno, "[LOGGER_BDA]70:71:bc:21:11:1e").get
      a should be (LoggerBDA("70:71:BC:21:11:1E"))
    }

    it("should parse scan element") {
      val a = parseAll(wifiScanAnno, "[WIFI_SCAN]2011/06/06 06:50:28").get
      a should be (WifiScan(TimeUtil.parse("2011-06-06 06:50:28")))

      val b = parseAll(btScanAnno, "[BT_SCAN]2011/06/06 06:50:48").get
      b should be (BtScan(TimeUtil.parse("2011-06-06 06:50:48")))
    }
  }

  describe("Other log") {
    it("should parse blank line") {
      parseAll(otherLog, "").get should be (BlankLine())
    }

    it("should parse invalid line as error log") {
      parseAll(otherLog, "fugafuga").get should be (ErrorLog("fugafuga"))
    }
  }

  describe("LogLine Parser") {
    it("should parse each element correctly") {
      parseAll(logLine, "[LOGGER_BDA]70:71:bc:21:11:1e").get should be (LoggerBDA("70:71:BC:21:11:1E"))
      parseAll(logLine, "[LOGGER_VERSION]1.0").get should be (LoggerVersion("1.0"))
      parseAll(logLine, "[BT_SCAN]2011/06/06 06:50:48").get should be (BtScan(TimeUtil.parse("2011-06-06 06:50:48")))
      parseAll(logLine, "[WIFI_SCAN]2011/06/06 06:50:28").get should be (WifiScan(TimeUtil.parse("2011-06-06 06:50:28")))

      val a = parseAll(logLine, "2011/06/06 06:49:46	pokutuna-MBA	58:55:CA:FB:56:D2").get
      val b = BtDetectLog(TimeUtil.parse("2011-06-06 06:49:46"), "pokutuna-MBA", "58:55:CA:FB:56:D2")
      a should be (b)

      val c = parseAll(logLine, "2011/06/06 06:49:44	TUNACAN	00:18:84:89:A9:74	-37").get
      val d = WifiDetectLog(TimeUtil.parse("2011-06-06 06:49:44"), "TUNACAN", "00:18:84:89:A9:74", -37)
      c should be (d)

      parseAll(logLine, "").get should be (BlankLine())
      parseAll(logLine, "fugafuga").get should be (ErrorLog("fugafuga"))
      parseAll(logLine, "2010/10/10 00:06:19     Kono_Pocket_PC  00:22:64:CD:9E:94fuga").get should be (ErrorLog("2010/10/10 00:06:19     Kono_Pocket_PC  00:22:64:CD:9E:94fuga"))
    }
  }

  describe("Read File") {
    it("should read dirty logfile") {
      val btlog = new FileReader("src/test/resources/test_btlogdata.tsv")
      LogParser.run(btlog).successful should be === true
    }
  }
}

