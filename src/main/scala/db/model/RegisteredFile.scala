package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class RegisteredFile(fileId: Pk[Int], fileName: String, md5hex: String) {
  def this(fileName: String, md5hex: String) = {
    this(NotAssigned, fileName, md5hex)
  }
}

object RegisteredFile {

  val tableName = "registered_files"

  val simple = {
    get[Pk[Int]]("file_id") ~
    get[String]("file_name") ~
    get[String]("md5_hex") map {
      case fileId~fileName~md5hex => RegisteredFile(fileId, fileName, md5hex)
    }
  }

  def insert(file: RegisteredFile)(implicit connection: Connection) = {
    SQL(
      "insert into " + tableName + "(file_name, md5_hex) values({fileName}, {md5hex})"
    ).on('fileName -> file.fileName, 'md5hex -> file.md5hex).executeUpdate()
  }

  def find(file: RegisteredFile)(implicit connection: Connection): Option[RegisteredFile] = {
    file.fileId match {
      case Id(_)       => findWithId(file)
      case NotAssigned => findIgnoreId(file)
    }
  }

  def findWithId(file: RegisteredFile)(implicit connection: Connection): Option[RegisteredFile] = {
    SQL(
      "select * from " + tableName + " where file_id = {fileId} and file_name = {fileName} and md5_hex = {md5hex} limit 1"
    ).on(
      'fileId -> file.fileId, 'fileName -> file.fileName, 'md5hex -> file.md5hex
    ).as(simple.singleOpt)
  }

  def findIgnoreId(file: RegisteredFile)(implicit connection: Connection): Option[RegisteredFile] = {
    SQL(
      "select * from " + tableName + " where file_name = {fileName} and md5_hex = {md5hex} limit 1"
    ).on('fileName -> file.fileName, 'md5hex -> file.md5hex).as(simple.singleOpt)
  }
}
