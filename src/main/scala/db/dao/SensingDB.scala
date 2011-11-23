package com.pokutuna.lifelog.db.dao

import java.io.File
import anorm._
import anorm.SqlParser._

class SensingDB(path: String) extends Database(path) with Schema {

  val schemaFile = new File("db/sensing.sql")
  implicit val num: Int = 1

}
