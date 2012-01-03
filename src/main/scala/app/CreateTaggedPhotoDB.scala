package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db.util._
import com.pokutuna.lifelog.parsing.LogParser
import com.pokutuna.lifelog.parsing.LogToken._
import com.pokutuna.lifelog.util._
import scala.annotation.tailrec

object CreateTaggedPhotoDB {

  class TaggedPhotoDBManager(path: String) {
    val db = new TaggedPhotoDB(path)

    db.applySchema()

    def copyPhotos(lifelog: LifelogDB) = {
      @tailrec
      def copyPhotosRec(lifelog: LifelogDB, offset:Int, limit: Int): Unit = {
        println("copying photos: " + offset)
        val photos = lifelog.photo(offset, limit).map(_.toSimplePhoto)
        db.insertPhoto(photos)
        if (photos.isEmpty) return else copyPhotosRec(lifelog, offset + limit, limit)
      }
      copyPhotosRec(lifelog, 0, 1000)
    }

    def createDevices(photo: SimplePhoto, sensing: SensingDB): Seq[Device] = {
      try {
        require(photo.dateTime != "")
        val start = DateTime.format(photo.dateTime).ago(minute = 3).asString
        val end = DateTime.format(photo.dateTime).fromNow(minute = 3).asString
        (sensing.btDetectedIn(start, end) ++ sensing.wifiDetectedIn(start, end)).map(_.toDevice).distinct
      } catch {
        case e: IllegalArgumentException =>
          println(photo.filename + " doesn't have dateTime field")
          List()
        case e =>
          println(photo.filename + " has error: " + e)
          e.printStackTrace()
          List()
      }
    }

    def createTags(photo: SimplePhoto, deviceIds: Seq[Int]): Seq[Tag] = {
      deviceIds.map(new Tag(_, photo.id.get))
    }

    def insertTagsAndDevices(sensing: SensingDB) = {
      @tailrec
      def insertTagsRec(sensing: SensingDB, offset: Int, limit: Int): Unit = {
        println("inserting tags: " + offset)
        val photos = db.photo(offset, limit)
        if (photos.isEmpty)
          return
        else {
          photos.foreach { p =>
            val devices = createDevices(p, sensing)
            val deviceIds = db.insertDevice(devices)
            val tags = createTags(p, deviceIds)
            db.insertTag(tags)
          }
          insertTagsRec(sensing, offset + limit, limit)
        }
      }
      insertTagsRec(sensing, 0, 1000)
    }
  }

  def run(sensing: Option[String], lifelog: Option[String], dbPath: Option[String]) = {
    val sensingDB = new SensingDB(sensing.getOrElse("sensing.db"))
    val lifelogDB = new LifelogDB(lifelog.getOrElse("lifelog.db"))

    val dbm = new TaggedPhotoDBManager(dbPath.getOrElse("tagged_photo.db"))
    dbm.copyPhotos(lifelogDB)
    dbm.insertTagsAndDevices(sensingDB)
  }

  def main(args: Array[String]) = {
    args.toList match {
      case a :: b :: c :: Nil => run(Some(a), Some(b), Some(c))
      case a :: b :: Nil      => run(Some(a), Some(b), None)
      case a :: Nil           => run(Some(a), None, None)
      case Nil                => run(None, None, None)
    }
  }
}
