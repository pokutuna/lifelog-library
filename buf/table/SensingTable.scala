package com.pokutuna.lifelog.db.table

import com.pokutuna.lifelog.db.model.SensingModel._
import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended.{ExtendedTable => Table, _}
import org.scalaquery.session.Database._

object SensingTable {

  abstract class DeviceTable[T <: DeviceRecord](_tableName:String) extends Table[T](_tableName) {
    def address = column[String]("address", O PrimaryKey, O NotNull)
    def name = column[String]("name", O Default "", O NotNull)
    def * : ColumnBase[T]
    def uIdx = index(_tableName + "_idx", address, unique = true)
  }

  object BtDeviceTable extends DeviceTable[BtDevice]("bt_devices") {
    def * = address ~ name <> (BtDevice, BtDevice.unapply _)
    def forInsert = address ~ name <>
      ({ (a, n) => BtDevice(a, n)}, { b: BtDevice => Some((b.address, b.name))})
  }

  object WifiDeviceTable extends DeviceTable[WifiDevice]("wifi_devices") {
    def * = address ~ name <> (WifiDevice, WifiDevice.unapply _)
    def forInsert = address ~ name <>
      ({ (a, n) => WifiDevice(a, n)}, { w: WifiDevice => Some((w.address, w.name))})
  }

  abstract class DetectedTable[T <: DetectedRecord](_tableName: String) extends Table[T](_tableName){
    def address = column[String]("address", O NotNull)
    def dateTime = column[String]("date_time", O NotNull)
    def * : ColumnBase[T] // address ~ dateTimeと定義しサブクラスで追加しようにもProjection2に推論されてダメになる
    def uIdx = index(_tableName + "_idx", address ~ dateTime, unique = true)
  }

  object BtDetectedTable extends DetectedTable[BtDetected]("bt_detected") {
    def * = address ~ dateTime <> (BtDetected, BtDetected.unapply _)
    def forInsert = address ~ dateTime <>
      ({ (a, d) => BtDetected(a, d)}, { b: BtDetected => Some((b.address, b.dateTime))})
  }

  object WifiDetectedTable extends DetectedTable[WifiDetected]("wifi_detected") {
    def strength = column[Int]("strength")
    def * = address ~ dateTime ~ strength <> (WifiDetected, WifiDetected.unapply _)
    def forInsert = address ~ dateTime ~ strength <>
      ({ (a, d, s) => WifiDetected(a, d, s)},
       { w: WifiDetected => Some((w.address, w.dateTime, w.strength))})
  }

}













