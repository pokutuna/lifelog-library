package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Tag(id: Pk[Int], deviceId: Int, photoId: Int, diffSec: Int) {
  def this(deviceId: Int, photoId: Int, diffSec: Int) = {
    this(NotAssigned, deviceId, photoId, diffSec)
  }

  def this(deviceId: Int, photoId: Int) = {
    this(deviceId, photoId, Tag.defaultDiffSec)
  }
}

object Tag {

  val tableName = "tags"

  val defaultDiffSec = 60 * 3

  val simple = {
    get[Pk[Int]]("id") ~ get[Int]("device_id") ~ get[Int]("photo_id") ~ get[Int]("diff_sec") map {
      case id~deviceId~photoId~diffSec => Tag(id, deviceId, photoId, diffSec)
    }
  }

  def insert(tag: Tag)(implicit connection: Connection): Int = {
    SQL(
      "insert into " + tableName + "(device_id, photo_id, diff_sec) values({deviceId}, {photoId}, {diffSec})"
    ).on(
      'deviceId -> tag.deviceId, 'photoId -> tag.photoId, 'diffSec -> tag.diffSec
    ).executeUpdate()
    SQL("select last_insert_rowid();").as(scalar[Int].single)
  }

  def find(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    tag.id match {
      case Id(_)       => findWithId(tag)
      case NotAssigned => findIgnoreId(tag)
    }
  }

  def findWithId(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where id = {id} and device_id = {deviceId} and photo_id = {photoId} and diff_sec = {diffSec} limit 1"
    ).on(
      'id -> tag.id, 'deviceId -> tag.deviceId, 'photoId -> tag.photoId, 'diffSec -> tag.diffSec
    ).as(simple.singleOpt)
  }

  def findIgnoreId(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where device_id = {deviceId} and photo_id = {photoId} and diff_sec = {diffSec} limit 1"
    ).on(
      'deviceId -> tag.deviceId, 'photoId -> tag.photoId, 'diffSec -> tag.diffSec
    ).as(simple.singleOpt)
  }

  def findById(id: Int)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where id = {id} limit 1"
    ).on('id -> id).as(simple.singleOpt)
  }

  def findByDeviceId(deviceId: Int, diffSec:Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where {minDiff} <= diff_sec and diff_sec <= {maxDiff} and device_id = {deviceId} order by photo_id"
    ).on('minDiff -> -diffSec, 'maxDiff -> diffSec, 'deviceId -> deviceId).as(simple *)
  }

  def findByDeviceId(deviceId: Int, offset: Int, limit: Int, diffSec:Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where {minDiff} <= diff_sec and diff_sec <= {maxDiff} and device_id = {deviceId} order by photo_id limit {limit} offset {offset}"
    ).on(
      'minDiff -> -diffSec, 'maxDiff -> diffSec, 'deviceId -> deviceId,
      'offset -> offset, 'limit -> limit
    ).as(simple *)
  }

  def findByPhotoId(photoId: Int, diffSec:Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where {minDiff} <= diff_sec and diff_sec <= {maxDiff} and photo_id = {photoId}"
    ).on('minDiff -> -diffSec, 'maxDiff -> diffSec, 'photoId -> photoId).as(simple *)
  }

  def countByDeviceId(deviceId: Int, diffSec:Int)(implicit connection: Connection): Int = {
    SQL(
      "select count(*) from " + tableName + " where {minDiff} <= diff_sec and diff_sec <= {maxDiff} and device_id = {deviceId}"
    ).on(
      'minDiff -> -diffSec, 'maxDiff -> diffSec, 'deviceId -> deviceId
    ).as(scalar[Int].single)
  }

  def countByPhotoId(photoId: Int, diffSec:Int)(implicit connection: Connection): Int = {
    SQL(
      "select count(*) from " + tableName + " where {minDiff} <= diff_sec and diff_sec <= {maxDiff} and photo_id = {photoId}"
    ).on(
      'minDiff -> -diffSec, 'maxDiff -> diffSec, 'photoId -> photoId
    ).as(scalar[Int].single)
  }

}
