package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db.util._
import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.util._
import scala.concurrent.ops._
import java.io.File

object CreateLifelogDB {

  class LifelogDBManager(path: String) {
    val db = new LifelogDB(path)
    db.applySchema()

    var size: Int = 0
    var photos: List[PhotoRecord] = List()

    def addPhoto(photo: PhotoRecord) = synchronized {
      size += 1
      photos = photo :: photos
      if (100 <= size) insert()
    }

    def insert() = synchronized {
      println("start insert")
      db.insertPhoto(photos)
      size = 0
      photos = List()
    }
  }

  val imageFilenamePattern = """\.jpg$|\.JPG$|\.png$|\.PNG$""".r

  def run(rootPath: String, dbPath: Option[String]) = {
    val dbm = new LifelogDBManager(dbPath.getOrElse("lifelog.db"))

    val files = FileSelector.select(new File(rootPath), imageFilenamePattern)
    spawn {
      files.par.foreach(f => dbm.addPhoto(PhotoRecordFactory(f)))
      dbm.insert()
    }
  }

  def main(args: Array[String]) = {
    val rootPath = args.headOption match {
      case Some(path) => path
      case None       => throw new RuntimeException("require rootPath for importing photos")
    }

    val dbPath = args.tail.headOption
    run(rootPath, dbPath)
  }
}
