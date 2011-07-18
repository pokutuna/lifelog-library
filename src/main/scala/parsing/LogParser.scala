package com.pokutuna.lifelog.parsing

import com.pokutuna.lifelog.parsing.LogToken._
import java.util.{Calendar, Date}
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.token.Tokens
import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.input.Reader

object LogParser extends RegexParsers {

  def run(in: CharSequence) = parseAll(log, in)
  def run(in: java.io.Reader) = parseAll(log, in)
  def run(in: Reader[Char]) = parseAll(log, in)

  override def skipWhitespace = false

  lazy val LF = elem('\n')
  lazy val Tab = elem('\t')

  lazy val log: Parser[List[LogLine]] = repsep(logLine, LF)

  lazy val logLine: Parser[LogLine] = detectLog | annotationLog | otherLog

  lazy val detectLog: Parser[DetectLog] = wifiDetect | btDetect

  lazy val btDetect: Parser[BtDetectLog] = dateTime ~ Tab ~ deviceName ~ Tab ~ address <~ ".*".r ^? {
    case time ~ _ ~ name ~ _ ~ addr => BtDetectLog(time, name, addr)
  }

  lazy val wifiDetect: Parser[WifiDetectLog] = dateTime ~ Tab ~ deviceName ~ Tab ~ address ~ Tab  ~ signal  <~ ".*".r ^? {
    case time ~ _ ~ name ~ _ ~ addr ~ _ ~ sig => WifiDetectLog(time, name, addr, sig)
  }

  //Time
  lazy val dateTime: Parser[Date] = date ~ """\s""".r ~ time ^^ {
    case d ~ _ ~ t =>
      val c = Calendar.getInstance
      c.clear()
      c.set(d._1, d._2 - 1, d._3, t._1, t._2, t._3)
      c.getTime
  }

  lazy val doubleDigits = """[0-9]{1,2}""".r

  lazy val timeSep = ":"
  lazy val time: Parser[(Int,Int,Int)] = hours ~ timeSep ~ minutes ~ timeSep ~ seconds ^^ {
    case h ~ _ ~ m ~ _ ~ s => (h, m, s)
  }
  lazy val hours: Parser[Int] = doubleDigits ^? {
    case h if 0 <= h.toInt && h.toInt <= 23 => h.toInt
  }

  lazy val minutes: Parser[Int] = doubleDigits ^? {
    case m if 0 <= m.toInt && m.toInt <= 59 => m.toInt
  }

  lazy val seconds: Parser[Int] = minutes

  lazy val dateSep = "-" | "/"
  lazy val date: Parser[(Int,Int,Int)] = year ~ dateSep ~ month ~ dateSep ~ day ^^ {
    case y ~ _ ~ m ~ _ ~ d => (y, m, d)
  }

  lazy val year: Parser[Int] = positiveDigit ~ rep(digit) ^? {
    case h ~ rest if (h + rest.mkString).toInt >= 1900  => (h + rest.mkString).toInt
  }

  lazy val month: Parser[Int] = doubleDigits ^? {
    case m if 0 < m.toInt && m.toInt <= 12 => m.toInt
  }

  lazy val day: Parser[Int] = doubleDigits ^? {
    case d if 0< d.toInt && d.toInt <= 31 => d.toInt
  }

  val positiveDigit: Parser[String] = """[1-9]""".r
  lazy val digit: Parser[String] = positiveDigit | "0".r

  //device name
  val deviceName: Parser[String] = """[^\f\n\r\t]*""".r

  //address
  lazy val address: Parser[String] = repN(5, hexPairWithColon) ~ hexPair ^? {
    case list ~ p => (list.mkString + p).toUpperCase
  }

  lazy val hexPairWithColon: Parser[String] = hexPair ~ ":" ^^ {
    case pair ~ colon => pair + colon
  }

  lazy val hexPair: Parser[String] = hexadecimal ~ hexadecimal ^^ {
    case first ~ second => first + second
  }

  val hexadecimal: Parser[String] = """[0-9a-fA-F]""".r

  //signal
  lazy val signal: Parser[Int] = "-".r.? ~ rep(digit) ^^ {
    case s ~ list => (s.getOrElse("") + list.mkString).toInt
  }

  //annotation
  lazy val annotationLog: Parser[Annotation] = versionAnno | loggerBdaAnno | btScanAnno | wifiScanAnno

  val versionTag = "LOGGER_VERSION"
  lazy val version = deviceName
  lazy val versionAnno: Parser[LoggerVersion] = "[" ~> versionTag ~> "]" ~> version ^^ {
    case v => LoggerVersion(v)
  }

  lazy val loggerBdaTag = "LOGGER_BDA"
  lazy val loggerBdaAnno: Parser[LoggerBDA] = "[" ~> loggerBdaTag ~> "]" ~> address ^^ {
    case a => LoggerBDA(a)
  }

  lazy val btScanTag = "BT_SCAN"
  lazy val btScanAnno: Parser[BtScan] = "[" ~> btScanTag ~> "]" ~> dateTime ^^ {
    case t => BtScan(t)
  }

  lazy val wifiScanTag = "WIFI_SCAN"
  lazy val wifiScanAnno: Parser[WifiScan] = "[" ~> wifiScanTag ~> "]" ~> dateTime ^^ {
    case t => WifiScan(t)
  }

  //other
  lazy val blankLine = """.*""".r ^? {
    case l if l == "" => BlankLine()
  }

  lazy val errorLog = """.*""".r ^? {
    case l => ErrorLog(l)
  }

  lazy val otherLog: Parser[OtherLog] = blankLine | errorLog
}
