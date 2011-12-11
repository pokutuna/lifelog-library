package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util._
import anorm._
import anorm.SqlParser._

class LifelogDB(path: String) extends Database(path) with Schema {

  val schemaUrl = Resource.getUrl("db/lifelog.sql")

  def photo(offset: Int, limit: Int) = {
    withConnection { implicit connection =>
      PhotoRecord.take(offset, limit)
    }
  }

  def photoTakenIn(start: String, end: String): Seq[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findByOrgDate(start, end)
    }
  }

  def photoTakenIn(start: String, end: String, offset: Int, limit: Int): Seq[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findByOrgDate(start, end, offset, limit)
    }
  }

  def photoTakenWhere(centerLat: Double, centerLon: Double, diff: Double): Seq[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findByLocation(centerLat, centerLon, diff)
    }
  }

  def photoById(id: Int): Option[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findById(id)
    }
  }

  def photoByName(filename: String): Seq[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findByName(filename)
    }
  }

  def photoByName(dirname: String, filename: String): Seq[PhotoRecord] = {
    withConnection { implicit connection =>
      PhotoRecord.findByName(dirname, filename)
    }
  }

  def existsFile(filename: String): Boolean = {
    !photoByName(filename).isEmpty
  }

  def existsFile(dirname: String, filename: String): Boolean = {
    !photoByName(dirname, filename).isEmpty
  }

  def insertPhoto(photo: PhotoRecord) = {
    withConnection { implicit connection =>
      PhotoRecord.insert(photo)
    }
  }

  def insertPhoto(photos: Seq[PhotoRecord]) = {
    withTransaction { implicit connection =>
      for (record <- photos) {
        PhotoRecord.insert(record)
      }
    }
  }

}
