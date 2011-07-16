package com.pokutuna.lifelog.parsing

import java.util.Date
import com.pokutuna.lifelog.parsing.Transformer._

object LogToken {

  trait LogLine //root trait

  //detects
  trait DetectLog extends LogLine with HasAddress with HasDateTime with HasDeviceName
  case class BtDetectLog(dateTime: Date, deviceName: String, address: String) extends DetectLog with ToBtDevice with ToBtDetected
  case class WifiDetectLog(dateTime: Date, deviceName: String, address: String, signal: Int) extends DetectLog with HasSignal with ToWifiDevice with ToWifiDetected

  //annotations
  trait Annotation extends LogLine
  case class LoggerVersion(version: String) extends Annotation
  case class LoggerBDA(address: String) extends Annotation with HasAddress
  trait ScanAnnotation extends Annotation with HasDateTime
  case class BtScan(dateTime: Date) extends ScanAnnotation
  case class WifiScan(dateTime: Date) extends ScanAnnotation

  //other
  trait OtherLog extends LogLine
  case class BlankLine() extends OtherLog
  case class ErrorLog(rawLine: String) extends OtherLog

  //field traits
  trait HasAddress {
    val address: String
  }

  trait HasDateTime {
    val dateTime: Date
  }

  trait HasDeviceName {
    val deviceName: String
  }

  trait HasSignal {
    val signal: Int
  }

}
