package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util._
import anorm._
import anorm.SqlParser._

class TaggedPhotoDB(path: String) extends Database(path) with Schema {

  val schemaUrl = Resource.getUrl("db/tagged_photo.sql")

  def insertTag(tag: Tag): Int = {
    withConnection { implicit connection =>
      Tag.insert(tag)
    }
  }

  def insertTag(tags: Seq[Tag]): Seq[Int] = {
    withTransaction { implicit connection =>
      tags.map(Tag.insert(_))
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

  def findTagByDeviceId(deviceId: Int): Seq[Tag] = {
    findTagByDeviceId(deviceId, Tag.defaultDiffSec)
  }

  def findTagByDeviceId(deviceId: Int, diffSec: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByDeviceId(deviceId, diffSec)
    }
  }

  def findTagByDeviceId(deviceId: Int, offset: Int, limit: Int): Seq[Tag] = {
    findTagByDeviceId(deviceId, Tag.defaultDiffSec, offset, limit)
  }

  def findTagByDeviceId(deviceId: Int, diffSec: Int, offset: Int, limit: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByDeviceId(deviceId, offset, limit, diffSec)
    }
  }

  def findTagByPhotoId(photoId: Int): Seq[Tag] = {
    findTagByPhotoId(photoId, Tag.defaultDiffSec)
  }

  def findTagByPhotoId(photoId: Int, diffSec: Int = Tag.defaultDiffSec): Seq[Tag] = {
    withConnection { implicit connection =>
      Tag.findByPhotoId(photoId, diffSec)
    }
  }

  def findTagByPhotoId(photoIds: Seq[Int], diffSec: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      photoIds.flatMap(Tag.findByPhotoId(_, diffSec))
    }
  }

  def findTagByAddress(address: String): Seq[Tag] = {
    findTagByAddress(address, Tag.defaultDiffSec)
  }

  def findTagByAddress(address: String, diffSec: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Device.findByAddress(address) match {
        case Some(d) => Tag.findByDeviceId(d.id.get, diffSec)
        case None    => Seq()
      }
    }
  }

  def findTagByAddress(address: String, offset: Int, limit: Int): Seq[Tag] = {
    findTagByAddress(address, Tag.defaultDiffSec, offset, limit)
  }

  def findTagByAddress(address: String, diffSec: Int, offset: Int, limit: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      Device.findByAddress(address) match {
        case Some(d) => Tag.findByDeviceId(d.id.get, offset, limit, diffSec)
        case None    => Seq()
      }
    }
  }

  def countTagByDeviceId(deviceId: Int, diffSec: Int = Tag.defaultDiffSec): Int = {
    withConnection { implicit connection =>
      Tag.countByDeviceId(deviceId, diffSec)
    }
  }

  def countTagByPhotoId(photoId: Int, diffSec: Int = Tag.defaultDiffSec): Int = {
    withConnection { implicit connection =>
      Tag.countByPhotoId(photoId, diffSec)
    }
  }

  def countTagBtByPhotoId(photoId: Int, diffSec: Int = Tag.defaultDiffSec): Int = {
    withConnection { implicit connection =>
      Tag.countBtByPhotoId(photoId, diffSec)
    }
  }

  def countTagWifiByPhotoId(photoId: Int, diffSec: Int = Tag.defaultDiffSec): Int = {
    withConnection { implicit connection =>
      Tag.countWifiByPhotoId(photoId, diffSec)
    }
  }

  def countTagByAddress(address: String): Int = {
    withConnection { implicit connection =>
      Device.findByAddress(address) match {
        case Some(d) => countTagByDeviceId(d.id.get)
        case None    => 0
      }
    }
  }

  def insertDevice(device: Device): Int = {
    withConnection { implicit connection =>
      Device.insertAsNeeded(device)
    }
  }

  def insertDevice(devices: Seq[Device]): Seq[Int] = {
    withConnection { implicit connection =>
      devices.map(Device.insertAsNeeded(_))
    }
  }

  def findDeviceById(id: Int):Option[Device] = {
    withConnection { implicit connection =>
      Device.findById(id)
    }
  }

  def findDeviceByAddress(address: String):Option[Device] = {
    withConnection { implicit connection =>
      Device.findByAddress(address)
    }
  }

  def insertPhoto(photo: SimplePhoto): Int = {
    withConnection { implicit connection =>
      SimplePhoto.insert(photo)
    }
  }

  def insertPhoto(photos: Seq[SimplePhoto]): Seq[Int] = {
    withTransaction { implicit connection =>
      photos.map(SimplePhoto.insert(_))
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
    findTagByPhotoDateTime(start, end, Tag.defaultDiffSec)
  }

  def findTagByPhotoDateTime(start: String, end: String, diffSec: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      SQL(
        "select tags.id, tags.device_id, tags.photo_id, tags.diff_sec from " + Tag.tableName + " inner join " + SimplePhoto.tableName + " on " + Tag.tableName + ".photo_id = " + SimplePhoto.tableName + ".id where  {minDiff} <= diff_sec and diff_sec <= {maxDiff} and {startTime} <= date_time and date_time <= {endTime} order by tags.id"
      ).on(
        'minDiff -> -diffSec, 'maxDiff -> diffSec, 'startTime -> start, 'endTime -> end
      ).as(Tag.simple *)
    }
  }

  def findTagByPhotoDateTime(start: String, end: String, offset: Int, limit: Int): Seq[Tag] = {
    findTagByPhotoDateTime(start, end, Tag.defaultDiffSec, offset, limit)
  }

  def findTagByPhotoDateTime(start: String, end: String, diffSec: Int, offset: Int, limit: Int): Seq[Tag] = {
    withConnection { implicit connection =>
      SQL(
        "select tags.id, tags.device_id, tags.photo_id, tags.diff_sec from " + Tag.tableName + " inner join " + SimplePhoto.tableName + " on " + Tag.tableName + ".photo_id = " + SimplePhoto.tableName + ".id where  {minDiff} <= diff_sec and diff_sec <= {maxDiff} and {startTime} <= date_time and date_time <= {endTime} order by tags.id limit {limit} offset {offset}"
      ).on(
        'minDiff -> -diffSec, 'maxDiff -> diffSec,
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

  def insertFavorite(label: String, deviceIds: Seq[Int]): Int = {
    withConnection { implicit connection =>
      Favorite.insertFavorite(label, deviceIds)
    }
  }

  def deleteFavorite(groupId: Int) = {
    withConnection { implicit connection =>
      Favorite.deleteFavorite(groupId)
    }
  }

  def allFavorites: Seq[Favorite] = {
    withConnection { implicit connection =>
      Favorite.allFavorites
    }
  }

  def findFavorite(groupId: Int): Option[Favorite] = {
    withConnection { implicit connection =>
      Favorite.findFavorite(groupId)
    }
  }

  def getInfo(key: String): Option[String] = {
    withConnection { implicit connection =>
      SQL(
        "select value from db_info where key = {key}"
      ).on('key -> key).as(scalar[String].singleOpt)
    }
  }

  def setInfo(key: String, value: String) = {
    getInfo(key) match {
      case Some(_) => updateInfo(key, value)
      case None    => insertInfo(key, value)
    }
  }

  def deleteInfo(key: String) = {
    withConnection { implicit connection =>
      SQL("delete from db_info where key = {key}").on('key -> key).executeUpdate()
    }
  }

  private def insertInfo(key: String, value: String) = {
    withConnection { implicit connection =>
      SQL(
        "insert into db_info(key, value) values({key}, {value})"
      ).on('key -> key, 'value -> value).executeUpdate()
    }
  }

  private def updateInfo(key: String, value: String) = {
    withConnection { implicit connection =>
      SQL(
        "update db_info set value = {value} where key = {key}"
      ).on('value -> value, 'key -> key).executeUpdate()
    }
  }

}
