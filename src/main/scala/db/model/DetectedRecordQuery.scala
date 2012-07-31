package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import com.pokutuna.lifelog.db.model._
import java.sql._

trait DetectedRecordQuery[T <: DetectedRecord] {

  val tableName: String
  val simple: RowParser[T]

  def insert(detected: T)(implicit connection: Connection): T

  def find(detected: T)(implicit connection: Connection): Option[T] = {
    SQL(
      "select * from " + tableName + " where address = {address} and date_time = {dateTime} and file_id = {fileId}"
    ).on(
      'address -> detected.address, 'dateTime -> detected.dateTime,
      'fileId -> detected.fileId
    ).as(simple.singleOpt)
  }

  def findByFileId(fileId: Int)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where file_id = {fileId}"
    ).on('fileId -> fileId).as(simple *)
  }

  def findByAddress(address: String)(implicit connection:Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where address = {address} order by date_time"
    ).on('address -> address).as(simple *)
  }

  def findByAddress(address: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where address = {address} order by date_time limit {limit} offset {offset}"
    ).on('address -> address, 'offset -> offset, 'limit -> limit).as(simple *)
  }

  def findByDateTime(start: String, end: String)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= date_time and date_time <= {endTime} order by date_time"
    ).on('startTime -> start, 'endTime -> end).as(simple *)
  }

  def findByDateTime(start: String, end: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= date_time and date_time <= {endTime} order by date_time limit {limit} offset {offset}"
    ).on(
      'startTime -> start, 'endTime -> end, 'limit -> limit, 'offset -> offset
    ).as(simple *)
  }

  def searchDatePrefix(datePrefix: String)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where date_time glob {dateTime} order by date_time"
    ).on('dateTime -> (datePrefix + "*")).as(simple *)
  }

  def searchDatePrefix(datePrefix: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where date_time glob {dateTime} order by date_time limit {limit} offset {offset}"
    ).on('dateTime -> (datePrefix + "*"), 'offset -> offset, 'limit -> limit).as(simple *)
  }

  def searchDatePrefixFilterAddress(datePrefix: String, address: String)(implicit connection: Connection): Seq[T] = {
    SQL(
      "select * from " + tableName + " where date_time glob {dateTime} and address = {address} order by date_time"
    ).on(
      'dateTime -> (datePrefix + "*"), 'address -> address
    ).as(simple *)
  }

  def countAddress(address: String)(implicit connection: Connection): Int = {
    SQL(
      "select count(*) from " + tableName + " where address = {address}"
    ).on('address -> address).as(scalar[Int].single)
  }

  def latestDateTime(implicit connection: Connection): String = {
    SQL(
      "select date_time from " + tableName + " order by date_time desc limit 1"
    ).as(scalar[String].single)
  }

  def oldestDateTime(implicit connection: Connection): String = {
    SQL(
      "select date_time from " + tableName + " order by date_time asc limit 1"
    ).as(scalar[String].single)
  }

  def detele(detected: T)(implicit connection: Connection) = {
    SQL(
      "delete from " + tableName + " where address = {address} and date_time = {dateTime} and file_id = {fileId}"
    ).on(
      'address -> detected.address, 'dateTime -> detected.dateTime,
      'fileId -> detected.fileId
    ).executeUpdate()
  }

  def insertUnique(detected: T)(implicit connection: Connection): Boolean = {
    find(detected) match {
      case Some(_) => false
      case None    => insert(detected); true
    }
  }

  def searchDatePrefixUniqueDevice(datePrefix: String)(implicit connection: Connection): Seq[_ <: DeviceRecord]

  def findNearestDetection(dateTime: String, address: String)(implicit connection: Connection): Option[T] = {
    SQL(
      "select * from " + tableName + " where address = {address} order by abs(strftime('%s', date_time) - strftime('%s', {dateTime})) asc limit 1"
    ).on('address -> address, 'dateTime -> dateTime).as(simple.singleOpt)
  }

  def calcNearestDetectionDiffSec(dateTime: String, address: String)(implicit connection: Connection): Option[Int] = {
    SQL(
      "select (strftime('%s', date_time) - strftime('%s', {dateTime})) from " + tableName + " where address = {address} order by abs(strftime('%s', date_time) - strftime('%s', {dateTime})) asc limit 1"
    ).on('address -> address, 'dateTime -> dateTime).as(scalar[Int].singleOpt)
  }
}
