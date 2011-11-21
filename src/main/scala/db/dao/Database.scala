package com.pokutuna.lifelog.db.dao

import anorm._
import java.sql._
import javax.sql._

class Database(path: String) {

  Class.forName("org.sqlite.JDBC")
  val databaseUrl = "jdbc:sqlite:" + path

  private def error = throw new Exception("database error in lifelog library")

  def getConnection: Connection = {
    DriverManager.getConnection(databaseUrl);
  }

  def withConnection[A](block: Connection => A): A = {
    val connection = getConnection
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  def withTransaction[A](block: Connection => A): A ={
    val connection = getConnection
    try {
      connection.setAutoCommit(false)
      val result = block(connection)
      connection.commit()
      result
    } catch {
      case e => connection.rollback()
      throw e
    } finally {
      connection.close()
    }
  }

}
