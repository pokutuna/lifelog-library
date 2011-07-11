package com.pokutuna.lifelog.db.dao

import scala.collection.JavaConverters._
import com.pokutuna.lifelog.db.model.LifelogModel._

class LifelogDAOForJava(path: String) {
  val dao = new LifelogDAO(path)

  def photoTakenIn(from: String, to: String): java.util.List[PhotoRecord] =
    dao.photoTakenIn(from, to).asJava

  def photoTakenWhere(latMin: Double, latMax: Double, lonMin: Double, lonMax: Double): java.util.List[PhotoRecord] =
    dao.photoTakenWhere(latMin, latMax, lonMin, lonMax).asJava

  def photoByName(filename: String): java.util.List[PhotoRecord] =
    dao.photoByName(filename).asJava

  def photoByName(dirname: String, filename: String): java.util.List[PhotoRecord] =
    dao.photoByName(dirname, filename).asJava

  def existsFile(dirname: String, filename: String): Boolean = dao.existsFile(dirname, filename)
  def existsFile(filename: String): Boolean = dao.existsFile(filename)

  def insertPhotoRecord(photo: PhotoRecord):Unit = dao.insertPhotoRecord(photo)
}
