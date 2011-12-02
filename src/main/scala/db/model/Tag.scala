package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Tag(id: Pk[Int], photoId: Int, address: String, deviceType: String) {
  def this(photoId: Int, address: String, deviceType: String) = {
    this(NotAssigned, photoId, address, deviceType)
  }
}

object Tag {

  val tableName = "tags"

  val simple = {
    get[Pk[Int]]("id") ~/
    get[Int]("photo_id") ~/
    get[String]("address") ~/
    get[String]("device_type") ^^ {
      case id~photoId~address~deviceType => Tag(id, photoId, address, deviceType)
    }
  }

  def insert(tag: Tag)(implicit connection: Connection) = {
    SQL(
      "insert into " + tableName + "(photo_id, address, device_type) values({photoId}, {address}, {deviceType})"
    ).on(
      'photoId -> tag.photoId, 'address -> tag.address, 'deviceType -> tag.deviceType
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
      "select * from " + tableName + " where id = {id} and photo_id = {photoId} and address = {address} and device_type = {deviceType} limit 1"
    ).on(
      'id -> tag.id, 'photoId -> tag.photoId, 'address -> tag.address,
      'deviceType -> tag.deviceType
    ).as(simple ?)
  }

  def findIgnoreId(tag: Tag)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where photo_id = {photoId} and address = {address} and device_type = {deviceType} limit 1"
    ).on(
      'photoId -> tag.photoId, 'address -> tag.address, 'deviceType -> tag.deviceType
    ).as(simple ?)
  }

  def findById(id: Int)(implicit connection: Connection): Option[Tag] = {
    SQL(
      "select * from " + tableName + " where id = {id} limit 1"
    ).on('id -> id).as(simple ?)
  }

  def findByAddress(address: String)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where address = {address} order by photo_id"
    ).on('address -> address).as(simple *)
  }

  def findByAddress(address: String, offset: Int, limit: Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where address = {address} order by photo_id limit {limit} offset {offset}"
    ).on('address -> address, 'offset -> offset, 'limit -> limit).as(simple *)
  }

  def findByPhotoId(photoId: Int)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where photo_id = {photoId}"
    ).on('photoId -> photoId).as(simple *)
  }

  def findByPhotoId(photoId: Int, address: String)(implicit connection: Connection): Seq[Tag] = {
    SQL(
      "select * from " + tableName + " where photo_id = {photoId} and address = {address}"
    ).on('photoId -> photoId, 'address -> address).as(simple *)
  }

  def countByAddress(address: String)(implicit connection: Connection): Int = {
    SQL(
      "select count(*) from " + tableName + " where address = {address}"
    ).on('address -> address).as(get[Int]("count(*)"))
  }

  def countByPhotoId(photoId: Int)(implicit connection: Connection): Int = {
    SQL(
      "select count(*) from " + tableName + " where photo_id = {photoId}"
    ).on('photoId -> photoId).as(get[Int]("count(*)"))
  }

}
