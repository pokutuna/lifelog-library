package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class SimplePhoto(
  id: Pk[Int],
  directory: String,
  filename: String,
  dateTime: String,
  latitude: Double,
  longitude: Double
) {
  def this(directory: String, filename: String, dateTime: String,
           latitude: Double, longitude: Double) = {
    this(NotAssigned, directory, filename, dateTime, latitude, longitude)
  }
}

object SimplePhoto {

  val tableName = "simple_photos"

  val simple = {
    get[Pk[Int]](tableName + ".id") ~/
    get[String](tableName + ".directory") ~/
    get[String](tableName + ".filename") ~/
    get[String](tableName + ".date_time") ~/
    get[Double](tableName + ".latitude") ~/
    get[Double](tableName + ".longitude") ^^ {
      case id~dir~name~orgDate~lat~lon => SimplePhoto(id, dir, name, orgDate, lat, lon)
    }
  }

  def insert(photo: SimplePhoto)(implicit connection: Connection) = {
    SQL(
      "insert into " + tableName + "(directory, filename, date_time, latitude, longitude) values({directory}, {filename}, {dateTime}, {latitude}, {longitude})"
    ).on(
      'directory -> photo.directory, 'filename -> photo.filename, 'dateTime -> photo.dateTime,
      'latitude -> photo.latitude, 'longitude -> photo.longitude
    ).executeUpdate()
  }

  def find(photo: SimplePhoto)(implicit connection: Connection): Option[SimplePhoto] = {
    photo.id match {
      case Id(_)       => findWithId(photo)
      case NotAssigned => findIgnoreId(photo)
    }
  }

  def findWithId(photo: SimplePhoto)(implicit connection: Connection): Option[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where id = {id} and directory = {directory} and filename = {filename} and date_time = {dateTime} and latitude = {lat} and longitude = {lon} limit 1"
    ).on(
      'id -> photo.id.get, 'directory -> photo.directory, 'filename -> photo.filename,
      'dateTime -> photo.dateTime, 'lat -> photo.latitude, 'lon -> photo.longitude
    ).as(simple ?)
  }

  def findIgnoreId(photo: SimplePhoto)(implicit connection: Connection): Option[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where directory = {directory} and filename = {filename} and date_time = {dateTime} and latitude = {lat} and longitude = {lon} limit 1"
    ).on(
      'directory -> photo.directory, 'filename -> photo.filename,
      'dateTime -> photo.dateTime, 'lat -> photo.latitude, 'lon -> photo.longitude
    ).as(simple ?)
  }

  def findById(id: Int)(implicit connection: Connection): Option[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where id = {id}"
    ).on('id -> id).as(simple ?)
  }

  def findByName(directory: String, filename: String)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where directory = {directory} and filename = {filename} order by date_time"
    ).on('directory -> directory, 'filename -> filename).as(simple *)
  }

  def findByName(filename: String)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where filename = {filename} order by date_time"
    ).on('filename -> filename).as(simple *)
  }

  def findByDateTime(start: String, end: String)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= date_time and date_time <= {endTime} order by date_time"
    ).on('startTime -> start, 'endTime -> end).as(simple *)
  }

  def findByDateTime(start: String, end: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= date_time and date_time <= {endTime} order by date_time limit {limit} offset {offset}"
    ).on(
      'startTime -> start, 'endTime -> end, 'offset -> offset, 'limit -> limit
    ).as(simple *)
  }

  def searchDatePrefix(datePrefix: String)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where date_time glob {dateTime} order by date_time"
    ).on('dateTime -> (datePrefix + "*")).as(simple *)
  }

  def searchDatePrefix(datePrefix: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where date_time glob {dateTime} order by date_time limit {limit} offset {offset}"
    ).on('dateTime -> (datePrefix + "*"), 'offset -> offset, 'limit -> limit).as(simple *)
  }

  def findByLocation(centerLat: Double, centerLon: Double, diff: Double)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where {minLat} <= latitude and latitude <= {maxLat} and {minLon} <= longitude and longitude <= {maxLon} order by date_time"
    ).on(
      'minLat -> (centerLat - diff), 'maxLat -> (centerLat + diff),
      'minLon -> (centerLon - diff), 'maxLon -> (centerLon + diff)
    ).as(simple *)
  }

  def findByLocation(centerLat: Double, centerLon: Double, diff: Double, offset: Int, limit: Int)(implicit connection: Connection): Seq[SimplePhoto] = {
    SQL(
      "select * from " + tableName + " where {minLat} <= latitude and latitude <= {maxLat} and {minLon} <= longitude and longitude <= {maxLon} order by date_time limit {limit} offset {offset}"
    ).on(
      'minLat -> (centerLat - diff), 'maxLat -> (centerLat + diff),
      'minLon -> (centerLon - diff), 'maxLon -> (centerLon + diff),
      'limit -> limit, 'offset -> offset
    ).as(simple *)
  }
}
