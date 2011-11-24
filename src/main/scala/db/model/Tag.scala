package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Tag(id: Pk[Int], photoId: Int, address: String, deviceType: String)

object Tag {

  val tableName = "tag"

  val simple = {
    get[Pk[Int]]("id") ~/
    get[Int]("photo_id") ~/
    get[String]("address") ~/
    get[String]("device_type") ^^ {
      case id~photoId~address~deviceType => Tag(id, photoId, address, deviceType)
    }
  }
}
