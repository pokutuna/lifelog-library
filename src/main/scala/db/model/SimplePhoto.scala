package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class SimplePhoto(
  id: Pk[Int],
  directory: String,
  filename: String,
  orgDate: String,
  latitude: Double,
  longitude: Double
)

object SimplePhoto {

  val tableName = "simple_photo"

  val simple = {
    get[Pk[Int]](tableName + ".id") ~/
    get[String](tableName + ".directory") ~/
    get[String](tableName + ".filename") ~/
    get[String](tableName + ".org_date") ~/
    get[Double](tableName + ".latitude") ~/
    get[Double](tableName + ".longitude") ^^ {
      case id~dir~name~orgDate~lat~lon => SimplePhoto(id, dir, name, orgDate, lat, lon)
    }
  }
}
