package com.pokutuna.Lifelog.db.dao

import org.scalaquery.session._
import org.scalaquery.ql._
import org.scalaquery.ql.extended._
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.session.Database._
import com.pokutuna.Lifelog.db.table.SensingTable._
import com.pokutuna.Lifelog.db.model.SensingModel._

class SensingDAO(path: String) extends DatabaseAccessObject(path, SQLiteDriver) {

  val tables = List(BtDeviceTable, BtDetectedTable, WifiDeviceTable, WifiDetectedTable)

  private def deviceDetectedIn[T <: DetectedRecord](from: String, to: String)(table: DetectedTable[T]): Seq[T] = {
    db.withSession {
      val q = for {
        b <- table.where(b => b.dateTime >= from && b.dateTime <= to)
        _ <- Query.orderBy(b.dateTime asc)
      } yield b
      q.list
    }
  }

  def detectedIn(from: String, to: String): Seq[DetectedRecord] = {
    (btDetectedIn(from, to) ++ wifiDetectedIn(from, to)).sortBy(_.dateTime)
  }

  def btDetectedIn(from: String, to: String): Seq[BtDetected] =
    deviceDetectedIn[BtDetected](from, to)(BtDetectedTable)

  def wifiDetectedIn(from: String, to:String): Seq[WifiDetected] =
    deviceDetectedIn(from, to)(WifiDetectedTable)

  def addressToName(address: String): Option[String] = {
    db.withSession {
      BtDeviceTable.where(_.address is address).firstOption.orElse{
        WifiDeviceTable.where(_.address is address).firstOption
      }
    } match {
      case Some(device) => Some(device.name)
      case None => None
    }
  }

  private def isAddressDevice(address: String)(table: DeviceTable[_ <: DeviceRecord]): Boolean = {
    db.withSession {
      table.where(_.address is address).firstOption match {
        case Some(_) => true
        case None    => false
      }
    }
  }

  def isBluetooth(address: String): Boolean = isAddressDevice(address)(BtDeviceTable)

  def isWifi(address: String): Boolean = isAddressDevice(address)(WifiDeviceTable)

  private def countDetectionWithTable(address: String)(table: DetectedTable[_ <: DetectedRecord]): Int = {
    db.withSession {
      val q = for(d <- table if d.address is address) yield d.dateTime.count
      q.first
    }
  }

  def countDetection(address: String): Int = {
    val table = if(isBluetooth(address)) BtDetectedTable else WifiDetectedTable
    countDetectionWithTable(address)(table)
  }

  def latestDate: String = {
    db.withSession {
      val bt = for {
        b <- BtDetectedTable
        _ <- Query.orderBy(b.dateTime desc)
      } yield b.dateTime

      val wf = for {
        w <- WifiDetectedTable
        _ <- Query.orderBy(w.dateTime desc)
      } yield w.dateTime

      (bt.firstOption, wf.firstOption) match {
        case (Some(b), Some(w)) => if(b > w) b else w
        case (Some(b), None) => b
        case (None, Some(w)) => w
        case (None, None) => throw new RuntimeException("db may have no detection record")
      }
    }
  }

  private def insertDevice(address: String, name: String)(table: DeviceTable[_ <: DeviceRecord]) {
    db.withSession {
      table.where(_.address is address).firstOption match {
        case Some(c) =>
          val currentName = { for(d <- table if d.address is address) yield d.name }
        if(currentName.first != name && name != "n/a") {
          println("name updated: " + currentName.first + " to " + name)
          currentName.update(name)
        }
        case None => (table.address ~ table.name).insert(address, name) 
          //table.forInsert.insert(address, name)
      }
    }
  }

  def insertBtDevice(address: String, name: String): Unit =
    insertDevice(address, name)(BtDeviceTable)

  def insertBtDevice(device: BtDevice): Unit =
    insertBtDevice(device.address, device.name)

  def insertWifiDevice(address: String, name: String): Unit =
    insertDevice(address, name)(WifiDeviceTable)

  def insertWifiDevice(device: WifiDevice): Unit =
    insertWifiDevice(device.address, device.name)

  private def existsDetected(address: String, dateTime: String)(table: DetectedTable[_ <: DetectedRecord]): Boolean = {
    db.withSession {
      table.where { t =>
        t.address is address
        t.dateTime is dateTime
      }.firstOption match {
        case Some(_) => true
        case None => false
      }
    }
  }

  def insertBtDetected(detection: BtDetected): Unit =
    insertBtDetected(detection.address, detection.dateTime)

  def insertBtDetected(address: String, dateTime:String): Unit = {
    if (!existsDetected(address, dateTime)(BtDetectedTable)) {
      db.withSession {
        BtDetectedTable.forInsert.insert(BtDetected(address, dateTime))
      }
    }
  }

  def insertWifiDetected(detection: WifiDetected): Unit =
    insertWifiDetected(detection.address, detection.dateTime, detection.strength)

  def insertWifiDetected(address: String, dateTime:String, strength: Int): Unit = {
    if (!existsDetected(address, dateTime)(WifiDetectedTable)) {
      db.withSession {
        WifiDetectedTable.forInsert.insert(WifiDetected(address, dateTime, strength))
      }
    }
  }

}

