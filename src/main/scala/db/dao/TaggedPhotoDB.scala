package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util._
import anorm._
import anorm.SqlParser._

class TaggedPhotoDB(path: String) extends Database(path) with Schema {

  val schemaUrl = Resource.getUrl("/db/tagged_photo.sql")

  def insertTag(tag: Tag) = {
    withConnection { implicit connection =>
      Tag.insert(tag)
    }
  }

  def insertTag(tags: Seq[Tag]) = {
    withTransaction { implicit connection =>
      for(tag <- tags) { Tag.insert(tag) }
    }
  }

  def findTag(tag: Tag): Option[Tag] = {
    withConnection { implicit connection =>
      Tag.find(tag)
    }
  }

  def findTagById(id: Int): Option[Tag] = {
    withConnection { implicit connection =>
      Tag.findById(id)
    }
  }

  def findTagByAddress(address: String): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByAddress(address)
    }
  }

  def findTagByAddress(address: String, offset: Int, limit: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByAddress(address, offset, limit)
    }
  }

  def findTagByPhotoId(photoId: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByPhotoId(photoId)
    }
  }

  def findTagByPhotoId(photoId: Int, address: String): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByPhotoId(photoId, address)
    }
  }

  def countTagByPhotoId(photoId: Int): Int = {
    withConnection { implicit connection =>
      Tag.countByPhotoId(photoId)
    }
  }

  def countTagByAddress(address: String): Int = {
    withConnection { implicit connection =>
      Tag.countByAddress(address)
    }
  }

  def insertPhoto(photo: SimplePhoto): Int = {
    withConnection { implicit connection =>
      SimplePhoto.insert(photo)
      SimplePhoto.find(photo).get.id.get
    }
  }

  def insertPhoto(photos: Seq[SimplePhoto]) = {
    withTransaction { implicit connection =>
      for(photo <- photos) { SimplePhoto.insert(photo) }
    }
  }

  def photo(offset: Int, limit: Int): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.take(offset, limit)
    }
  }

  def findPhoto(photo: SimplePhoto): Option[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.find(photo)
    }
  }

  def findPhotoById(id: Int): Option[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findById(id)
    }
  }

  def findPhotoByName(directory: String, filename: String): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByName(directory, filename)
    }
  }

  def findPhotoByName(filename: String): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByName(filename)
    }
  }

  def findPhotoByDateTime(start: String, end: String): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByDateTime(start, end)
    }
  }

  def findPhotoByDateTime(start: String, end: String, offset: Int, limit: Int): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByDateTime(start, end, offset, limit)
    }
  }

  def findPhotoByDatePrefix(datePrefix: String): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.searchDatePrefix(datePrefix)
    }
  }

  def findPhotoByDatePrefix(datePrefix: String, offset: Int, limit: Int): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.searchDatePrefix(datePrefix, offset, limit)
    }
  }

  def findPhotoByLocation(centerLat: Double, centerLon: Double, diff: Double): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByLocation(centerLat, centerLon, diff)
    }
  }

  def findPhotoByLocation(centerLat: Double, centerLon: Double, diff: Double, offset: Int, limit: Int): Seq[SimplePhoto] = {
    withConnection { implicit connection =>
      SimplePhoto.findByLocation(centerLat, centerLon, diff, offset, limit)
    }
  }

  def findTagByPhotoDateTime(start: String, end: String): Seq[Tag] = {
    withConnection { implicit connection =>
      SQL(
        "select tags.id, tags.photo_id, tags.address, tags.device_type from " + Tag.tableName + " inner join " + SimplePhoto.tableName + " on " + Tag.tableName + ".photo_id = " + SimplePhoto.tableName + ".id where {startTime} <= date_time and date_time <= {endTime} order by tags.id, date_time"
      ).on(
        'startTime -> start, 'endTime -> end
      ).as(Tag.simple *)
    }
  }

  def findTagByPhotoDateTime(start: String, end: String, offset: Int, limit: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      SQL(
        "select tags.id, tags.photo_id, tags.address, tags.device_type from " + Tag.tableName + " inner join " + SimplePhoto.tableName + " on " + Tag.tableName + ".photo_id = " + SimplePhoto.tableName + ".id where {startTime} <= date_time and date_time <= {endTime} order by tags.id, date_time limit {limit} offset {offset}"
      ).on(
        'startTime -> start, 'endTime -> end, 'limit -> limit, 'offset -> offset
      ).as(Tag.simple *)
    }
  }

  def latestDate: String = {
    withConnection { implicit connection =>
      SimplePhoto.latestDate
    }
  }

  def oldestDate: String = {
    withConnection { implicit connection =>
      SimplePhoto.oldestDate
    }
  }
}
