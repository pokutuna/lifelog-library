package com.pokutuna.lifelog.parsing

import java.util.Date
import java.util.Calendar
import scala.util.parsing.combinator._
import scala.util.parsing.input.Reader
import com.pokutuna.lifelog.parsing.LogToken._

object LogParser extends RegexParsers {

  def run(in: CharSequence) = parseAll(log, in)
  def run(in: java.io.Reader) = parseAll(log, in)
  def run(in: Reader[Char]) = parseAll(log, in)

  override def skipWhitespace = false

  def EOL: Parser[String] = "\n"
  def tab: Parser[String] = "\t"

  def log: Parser[List[LogLine]] = rep(logLine)

  def logLine: Parser[LogLine] = (detectLog | annotationLog | otherLog) <~ EOL

  def detectLog: Parser[DetectLog] = wifiDetect | btDetect

  def btDetect: Parser[BtDetectLog] = dateTime ~ tab ~ deviceName ~ tab ~ address ^^ {
    case time ~ _ ~ name ~ _ ~ addr => BtDetectLog(time, name, addr)
  }

  def wifiDetect: Parser[WifiDetectLog] = dateTime ~ tab ~ deviceName ~ tab ~ address ~ tab  ~ signal ^^ {
    case time ~ _ ~ name ~ _ ~ addr ~ _ ~ sig => WifiDetectLog(time, name, addr, sig)
  }

  //Time
  def dateTime: Parser[Date] = date ~ """\s""".r ~ time ^^ {
    case d ~ _ ~ t =>
      val c = Calendar.getInstance
      c.clear()
      c.set(d._1, d._2 - 1, d._3, t._1, t._2, t._3)
      c.getTime
  }

  def doubleDigits = """[0-9]{1,2}""".r

  def timeSep = ":"
  def time: Parser[(Int,Int,Int)] = hours ~ timeSep ~ minutes ~ timeSep ~ seconds ^^ {
    case h ~ _ ~ m ~ _ ~ s => (h, m, s)
  }
  def hours: Parser[Int] = doubleDigits ^? {
    case h if 0 <= h.toInt && h.toInt <= 23 => h.toInt
  }

  def minutes: Parser[Int] = doubleDigits ^? {
    case m if 0 <= m.toInt && m.toInt <= 59 => m.toInt
  }

  def seconds: Parser[Int] = minutes

  def dateSep = "-" | "/"
  def date: Parser[(Int,Int,Int)] = year ~ dateSep ~ month ~ dateSep ~ day ^^ {
    case y ~ _ ~ m ~ _ ~ d => (y, m, d)
  }

  def year: Parser[Int] = positiveDigit ~ rep(digit) ^? {
    case h ~ rest if (h + rest.mkString).toInt >= 1900  => (h + rest.mkString).toInt
  }

  def month: Parser[Int] = doubleDigits ^? {
    case m if 0 < m.toInt && m.toInt <= 12 => m.toInt
  }

  def day: Parser[Int] = doubleDigits ^? {
    case d if 0< d.toInt && d.toInt <= 31 => d.toInt
  }

  def positiveDigit: Parser[String] = """[1-9]""".r
  def digit: Parser[String] = positiveDigit | "0".r

  //device name
  def deviceName: Parser[String] = """[^\f\n\r\t]*""".r

  //address
  def address: Parser[String] = repN(5, hexPairWithColon) ~ hexPair ^^ {
    case list ~ p => (list.mkString + p).toUpperCase
  }

  def hexPairWithColon: Parser[String] = hexPair ~ ":" ^^ {
    case pair ~ colon => pair + colon
  }

  def hexPair: Parser[String] = hexadecimal ~ hexadecimal ^^ {
    case first ~ second => first + second
  }

  def hexadecimal: Parser[String] = """[0-9a-fA-F]""".r

  //signal
  def signal: Parser[Int] = "-".r.? ~ rep(digit) ^^ {
    case s ~ list => (s.getOrElse("") + list.mkString).toInt
  }

  //annotation
  def annotationLog: Parser[Annotation] = versionAnno | loggerBdaAnno | btScanAnno | wifiScanAnno

  val versionTag = "LOGGER_VERSION"
  def version = deviceName
  def versionAnno: Parser[LoggerVersion] = "[" ~> versionTag ~> "]" ~> version ^^ {
    case v => LoggerVersion(v)
  }

  def loggerBdaTag = "LOGGER_BDA"
  def loggerBdaAnno: Parser[LoggerBDA] = "[" ~> loggerBdaTag ~> "]" ~> address ^^ {
    case a => LoggerBDA(a)
  }

  def btScanTag = "BT_SCAN"
  def btScanAnno: Parser[BtScan] = "[" ~> btScanTag ~> "]" ~> dateTime ^^ {
    case t => BtScan(t)
  }

  def wifiScanTag = "WIFI_SCAN"
  def wifiScanAnno: Parser[WifiScan] = "[" ~> wifiScanTag ~> "]" ~> dateTime ^^ {
    case t => WifiScan(t)
  }

  //other
  def otherLog: Parser[OtherLog] = """.*""".r ^^ {
    case blank if blank == "" => BlankLine()
    case e => ErrorLog(e)
  }
}
