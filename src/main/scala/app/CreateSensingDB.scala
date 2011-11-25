package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db.util._
import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.parsing.LogParser
import com.pokutuna.lifelog.parsing.LogToken._
import com.pokutuna.lifelog.util._
import java.io.FileReader
import scala.concurrent.ops._
import java.io.File

object CreateSensingDB {

  class SensingDBManager(path: String) {
    val db = new SensingDB(path)

    db.applySchema()

    def insert(file: File, logs: Seq[DetectLog]) = synchronized {
      println("inserting: " + file.getName)
      val id = db.insertRegisteredFile(RegisteredFileFactory(file))
      val detects: Seq[DetectedRecord] = logs.map(_.toDetectRecord(id))
      val devices: Seq[DeviceRecord] = logs.map(_.toDeviceRecord)

      detects.headOption match {
        case Some(b) if b.isInstanceOf[BtDetected] =>
          db.insertBtDetected(detects.asInstanceOf[Seq[BtDetected]])
          db.insertBtDevice(devices.asInstanceOf[Seq[BtDevice]])
        case Some(w) if w.isInstanceOf[WifiDetected] =>
          db.insertWifiDetected(detects.asInstanceOf[Seq[WifiDetected]])
          db.insertWifiDevice(devices.asInstanceOf[Seq[WifiDevice]])
        case None =>
      }

    }
  }

  val logFilenamePattern = """(bda|wifi).*\.tsv""".r

  def filterDetectLog(logs: Seq[LogLine]): Seq[DetectLog] = {
    logs.map { l =>
      l match {
        case d: DetectLog => Some(d)
        case _            => None
      }
    }.flatMap(d => d)
  }

  def run(rootPath: String, dbPath: Option[String]) = {
    val dbm = new SensingDBManager(dbPath match {
      case Some(name) => name
      case None       => "sensing.db"
    })

    val files = FileSelector.select(new File(rootPath), logFilenamePattern)
    files.par.foreach { file =>
      println("parsing: " + file.getName)
      try {
        val parsed = filterDetectLog(LogParser.run(new FileReader(file)).get)
        dbm.insert(file, parsed)
      } catch {
        case e =>
          println(e)
          println("logfile error: " + file.getName())
      }
    }
  }

  def main(args: Array[String]) = {
    val rootPath = args.headOption match {
      case Some(path) => path
      case None       => throw new RuntimeException("require rootPath for importing photos")
    }

    val dbPath = args.tail.headOption
    run(rootPath, dbPath)
  }
}
