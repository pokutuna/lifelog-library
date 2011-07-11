package com.pokutuna.lifelog.db.table

import org.scalaquery.session._
import org.scalaquery.ql._
import org.scalaquery.ql.extended._
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.session.Database._
import com.pokutuna.lifelog.db.model.LifelogModel._

object LifelogTable {
  object Photos extends Table[PhotoRecord]("photo"){
    def directory = column[String]("directory")
    def filename = column[String]("filename")
    def orgDate = column[String]("org_date")
    def dateTime = orgDate
    def latitude = column[Double]("latitude")
    def longitude = column[Double]("longitude")
    def width = column[Int]("width")
    def height = column[Int]("height")
    def fileSize = column[Int]("file_size")
    def year = column[Int]("year")
    def month = column[Int]("month")
    def day = column[Int]("day")
    def hour = column[Int]("hour")
    def minute = column[Int]("minute")
    def second = column[Int]("second")

    def * = directory ~ filename ~ orgDate ~ latitude ~ longitude ~ width ~ height ~ fileSize ~ year ~ month ~ day ~ hour ~ minute ~ second <>
      (PhotoRecord, PhotoRecord.unapply _)

    def forInsert = directory ~ filename ~ orgDate ~ latitude ~ longitude ~ width ~ height ~ fileSize ~ year ~ month ~ day ~ hour ~ minute ~ second <>
    ({ (dir, f, o, la, lo, wi, he, fs, y, m, d, h, min, s) => PhotoRecord(dir, f, o, la, lo, wi, he, fs, y, m, d, h, min, s)},
     { p:PhotoRecord => Some((p.directory, p.filename, p.orgDate, p.latitude, p.longitude, p.width, p.height, p.fileSize, p.year, p.month, p.day, p.hour, p.minute, p.second))})
  }
}
