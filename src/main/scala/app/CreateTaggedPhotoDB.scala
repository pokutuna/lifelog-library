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

    def createTag(photo: SimplePhoto, sensing: SensingDB): Seq[Tag] = {
      try {
        val start = DateTime.format(photo.dateTime).ago(minute = 3).asString
        val end = DateTime.format(photo.dateTime).fromNow(minute = 3).asString
//        (sensing.btDetectedIn(start, end) ++ sensing.wifiDetectedIn(start, end)).map(_.toTag(photo.id.get)).distinct
        Seq[Tag]() //TODO
      } catch {
        case e =>
          println(photo.filename + " has error : " + e)
          List()
      }
    }

    def insertTags(sensing: SensingDB) = {
      @tailrec
      def insertTagsRec(sensing: SensingDB, offset: Int, limit: Int): Unit = {
        println("inserting tags: " + offset)
        val photos = db.photo(offset, limit)
        if (photos.isEmpty)
          return
        else {
          val tags = photos.flatMap(createTag(_, sensing))
          db.insertTag(tags)
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
    dbm.insertTags(sensingDB)
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
