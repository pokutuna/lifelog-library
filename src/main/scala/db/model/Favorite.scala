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

case class Favorite(group: FavoriteGroup, devices: Seq[FavoriteDevice]) {
  //def this(label, )
}

object Favorite {

  val groupTableName = "favorite_groups"

  val groupSimple = {
    get[Pk[Int]]("id") ~ get[String]("label") ^^ {
      case id~label => FavoriteGroup(id, label)
    }
  }

  val deviceTableName = "favorite_devices"

  val deviceSimple = {
    get[Pk[Int]]("id") ~/
    get[Int]("group_id") ~/
    get[Int]("device_id") ^^ {
      case id~groupId~deviceId => FavoriteDevice(id, groupId, deviceId)
    }
  }
}
