package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model._
import scala.collection.JavaConverters._

class LifelogDBForJava(path: String) {
  val dao = new LifelogDB(path)

  def photoTakenIn(from: String, to: String): java.util.List[PhotoRecord] =
    dao.photoTakenIn(from, to).asJava

  def photoTakenWhere(latMin: Double, latMax: Double, diff: Double): java.util.List[PhotoRecord] =
    dao.photoTakenWhere(latMin, latMax, diff).asJava

  def photoByName(filename: String): java.util.List[PhotoRecord] =
    dao.photoByName(filename).asJava

  def photoByName(dirname: String, filename: String): java.util.List[PhotoRecord] =
    dao.photoByName(dirname, filename).asJava

  def existsFile(dirname: String, filename: String): Boolean = dao.existsFile(dirname, filename)
  def existsFile(filename: String): Boolean = dao.existsFile(filename)

  def insertPhotoRecord(photo: PhotoRecord):Unit = dao.insertPhoto(photo)
}
