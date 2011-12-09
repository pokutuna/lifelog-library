package com.pokutuna.lifelog.db.dao

import anorm._
import java.net.URL
import scala.io.Source

trait Schema {
  self: Database =>

  val schemaUrl: URL

  def readSchema: String = {
    Source.fromURL(schemaUrl).mkString
  }

  def schemaStatements: Seq[String] = {
    readSchema.split(";").map(_.trim).filterNot( str =>
      str.startsWith("/*") ||
      str.startsWith("--") ||
      str.isEmpty
    ).map(_ + ";")
  }

  def applySchema() = {
    withConnection { implicit c =>
      self.schemaStatements.foreach { statement =>
        SQL(statement).executeUpdate()
      }
    }
  }
}
