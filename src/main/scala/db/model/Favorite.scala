package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class FavoriteGroup(id: Pk[Int], label: String) {
  def this(label: String) = this(NotAssigned, label)
}

case class FavoriteDevice(id: Pk[Int], groupId: Int, deviceId: Int) {
  def this(groupId: Int, deviceId: Int) = {
    this(NotAssigned, groupId, deviceId)
  }
}

case class Favorite(group: FavoriteGroup, devices: Seq[FavoriteDevice])

object Favorite {

  val groupTableName = "favorite_groups"

  val groupSimple = {
    get[Pk[Int]]("id") ~ get[String]("label") map {
      case id~label => FavoriteGroup(id, label)
    }
  }

  val deviceTableName = "favorite_devices"

  val deviceSimple = {
    get[Pk[Int]]("id") ~ get[Int]("group_id") ~ get[Int]("device_id") map {
      case id~groupId~deviceId => FavoriteDevice(id, groupId, deviceId)
    }
  }

  def allFavorites(implicit connection: Connection): Seq[Favorite] = {
    val groups = allFavoriteGroup
    groups.map { group =>
      Favorite(group, findFavoriteDeviceByGroupId(group.id.get))
    }
  }

  def allFavoriteGroup(implicit connection: Connection): Seq[FavoriteGroup] = {
    SQL("select * from " + groupTableName + " order by id").as(groupSimple *)
  }

  def findFavoriteDeviceByGroupId(groupId: Int)(implicit connection: Connection): Seq[FavoriteDevice] = {
    SQL(
      "select * from " + deviceTableName + " where group_id = {groupId} order by id"
    ).on('groupId -> groupId).as(deviceSimple *)
  }

  def findFavorite(id: Int)(implicit connection: Connection): Option[Favorite] = {
    val group = SQL(
      "select * from " + groupTableName + " where id = {id}"
    ).on('id -> id).as(groupSimple.singleOpt)

    group match {
      case Some(g) => Some(Favorite(g, findFavoriteDeviceByGroupId(g.id.get)))
      case None    => None
    }
  }

  def insertFavorite(label: String, deviceIds: Seq[Int])(implicit connection: Connection): Int = {
    val groupId = insertFavoriteGroup(label)
    deviceIds.foreach(insertFavoriteDevice(groupId, _))
    return groupId
  }

  def deleteFavorite(groupId: Int)(implicit connection: Connection) = {
    deleteFavoriteGroup(groupId)
    deleteFavoriteDeviceByGroupId(groupId)
  }

  def insertFavoriteGroup(label: String)(implicit connection: Connection): Int = {
    SQL(
      "insert into " + groupTableName + "(label) values ({label})"
    ).on('label -> label).executeUpdate()
    SQL("select last_insert_rowid();").as(scalar[Int].single)
  }

  def deleteFavoriteGroup(id: Int)(implicit connection: Connection) = {
    SQL("delete from " + groupTableName + " where id = {id}").on('id -> id).executeUpdate
  }

  def insertFavoriteDevice(groupId: Int, deviceId: Int)(implicit connection: Connection): Int = {
    SQL(
      "insert into " + deviceTableName + "(group_id, device_id) values ({groupId}, {deviceId})"
    ).on('groupId -> groupId, 'deviceId -> deviceId).executeUpdate()
    SQL("select last_insert_rowid();").as(scalar[Int].single)
  }

  def deleteFavoriteDeviceByGroupId(groupId: Int)(implicit connection: Connection) = {
    SQL(
      "delete from " + deviceTableName + " where group_id = {groupId}"
    ).on('groupId -> groupId).executeUpdate()
  }
}
