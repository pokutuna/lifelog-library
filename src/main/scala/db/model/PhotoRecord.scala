package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class PhotoRecord(
  directory: String,
  filename: String,
  orgDate: String,
  latitude: Double,
  longitude: Double,
  width: Int,
  height: Int,
  fileSize: Int,
  year: Int,
  month: Int,
  day: Int,
  hour: Int,
  minute: Int,
  second: Int,
  comment: String
) {
  def toSimplePhoto: SimplePhoto = {
    new SimplePhoto(directory, filename, orgDate, latitude, longitude)
  }
}

object PhotoRecord {

  val tableName = "photo"

  def simple = {
    get[String]("directory") ~/
    get[String]("filename") ~/
    get[String]("org_date") ~/
    get[Double]("latitude") ~/
    get[Double]("longitude") ~/
    get[Int]("width") ~/
    get[Int]("height") ~/
    get[Int]("file_size") ~/
    get[Int]("year") ~/
    get[Int]("month") ~/
    get[Int]("day") ~/
    get[Int]("hour") ~/
    get[Int]("minute") ~/
    get[Int]("second") ~/
    get[String]("comment") ^^ {
      case directory~fielname~orgDate~latitude~longitude~width~height~fileSize~year~month~day~hour~minute~second~comment =>
        PhotoRecord(directory, fielname, orgDate, latitude, longitude, width, height, fileSize, year, month, day, hour, minute, second, comment)
    }
  }

  def insert(photo: PhotoRecord)(implicit connection: Connection): PhotoRecord = {
    SQL(
      "insert into " + tableName + "(directory, filename, org_date, latitude, longitude, width, height, file_size, year, month, day, hour, minute, second, comment) values ({directory}, {filename}, {orgDate}, {latitude}, {longitude}, {width}, {height}, {fileSize}, {year}, {month}, {day}, {hour}, {minute}, {second}, {comment})"
    ).on(
      'directory -> photo.directory, 'filename -> photo.filename, 'orgDate -> photo.orgDate,
      'latitude -> photo.latitude, 'longitude -> photo.longitude, 'width -> photo.width,
      'height -> photo.height, 'fileSize -> photo.fileSize, 'year -> photo.year,
      'month -> photo.month, 'day -> photo.day, 'hour -> photo.hour, 'minute -> photo.minute,
      'second -> photo.second, 'comment -> photo.comment
    ).executeUpdate()
    return photo
  }

  def findByName(filename: String)(implicit connection: Connection): Seq[PhotoRecord] = {
    SQL(
      "select * from " + tableName + " where filename = {filename} order by org_date"
    ).on('filename -> filename).as(simple *)
  }

  def findByName(directory: String, filename: String)(implicit connection: Connection): Seq[PhotoRecord] = {
    SQL(
      "select * from " + tableName + " where directory = {directory} and filename = {filename} order by org_date"
    ).on('directory -> directory, 'filename -> filename).as(simple *)
  }

  def findByOrgDate(start: String, end: String)(implicit connection: Connection): Seq[PhotoRecord] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= org_date and org_date <= {endTime} order by org_date"
    ).on('startTime -> start, 'endTime -> end).as(simple *)
  }

  def findByOrgDate(start: String, end: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[PhotoRecord] = {
    SQL(
      "select * from " + tableName + " where {startTime} <= org_date and org_date <= {endTime} order by org_date limit {limit} offset {offset}"
    ).on(
      'startTime -> start, 'endTime -> end, 'offset -> offset, 'limit -> limit
    ).as(simple *)
  }

  def findByLocation(centerLat: Double, centerLon: Double, diff: Double)(implicit connection: Connection): Seq[PhotoRecord] = {
    SQL(
      "select * from " + tableName + " where {minLat} <= latitude and latitude <= {maxLat} and {minLon} <= longitude and longitude <= {maxLon} order by org_date"
    ).on(
      'minLat -> (centerLat - diff), 'maxLat -> (centerLat + diff),
      'minLon -> (centerLon - diff), 'maxLon -> (centerLon + diff)
    ).as(simple *)
  }

}
