package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Tag(id: Pk[Int], deviceId: Int, photoId: Int) {
  def this(deviceId: Int, photoId: Int) = {
    this(NotAssigned, deviceId, photoId)
  }
}

object Tag {

  val tableName = "tags"

  val simple = {
    get[Pk[Int]]("id") ~/
    get[Int]("device_id") ~/
    get[Int]("photo_id") ^^ {
      case id~deviceId~photoId => Tag(id, deviceId, photoId)
    }
  }

  def insert(tag: Tag)(implicit connection: Connection) = {
    SQL(
      "insert into " + tableName + "(device_id, photo_id) values({deviceId}, {photoId})"
    ).on(
      'deviceId -> tag.deviceId, 'photoId -> tag.photoId
    ).executeUpdate()
  }

  def find(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    tag.id match {
      case Id(_)       => findWithId(tag)
      case NotAssigned => findIgnoreId(tag)
    }
  }

  def findWithId(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where id = {id} and device_id = {deviceId} and photo_id = {photoId} limit 1"
    ).on(
      'id -> tag.id, 'deviceId -> tag.deviceId, 'photoId -> tag.photoId
    ).as(simple ?)
  }

  def findIgnoreId(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where device_id = {deviceId} and photo_id = {photoId} limit 1"
    ).on(
      'deviceId -> tag.deviceId, 'photoId -> tag.photoId
    ).as(simple ?)
  }

  def findById(id: Int)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where id = {id} limit 1"
    ).on('id -> id).as(simple ?)
  }

  def findByDeviceId(deviceId: Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where device_id = {deviceId} order by photo_id"
    ).on('deviceId -> deviceId).as(simple *)
  }

  def findByDeviceId(deviceId: Int, offset: Int, limit: Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where device_id = {deviceId} order by photo_id limit {limit} offset {offset}"
    ).on('deviceId -> deviceId, 'offset -> offset, 'limit -> limit).as(simple *)
  }

  def findByPhotoId(photoId: Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where photo_id = {photoId}"
    ).on('photoId -> photoId).as(simple *)
  }

}
