package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util.DateTime.Implicit._
import anorm._
import anorm.SqlParser._
import scala.annotation.tailrec
import scala.collection.JavaConverters._
import java.io._


object HDRDiet {

  val db = new TaggedPhotoDB("diet_tagged_photo.db")

  def dietAllPhotos() = {
    @tailrec
    def rec(id: Int): Unit = {
      val photo = getSinglePhoto(id)
      photo match {
        case Some(p) => dietPhoto(p); rec(p.id.get)
        case None    => return
      }
    }
    rec(-1)
  }

  def getSinglePhoto(smallIdNotInclude: Int): Option[SimplePhoto] = {
    db.withConnection { implicit con =>
      SQL(
        "select * from simple_photos where {id} < id order by id limit 1"
      ).on('id -> smallIdNotInclude).as(SimplePhoto.simple.singleOpt)
    }
  }

  def dietPhoto(photo: SimplePhoto) = {
    println("diet " + photo.toString)
    try {
      val relates = findRelatedHDRLike(photo)
      relates.map { r =>
        if(r.id.get != photo.id.get) deletePhoto(r)
      }
    } catch { case e: IllegalArgumentException => println("error") }
  }

  def findRelatedHDRLike(photo: SimplePhoto): List[SimplePhoto] = {
    require(photo.dateTime != "")
    db.withConnection { implicit con =>
      SQL(
        "select * from simple_photos where {startTime} <= date_time and date_time <= {endTime} and {lat} = latitude and {lon} = longitude order by date_time"
      ).on(
        'startTime -> photo.dateTime.ago(second = 3).asString,
        'endTime   -> photo.dateTime.fromNow(second = 3).asString,
        'lat       -> photo.latitude,
        'lon       -> photo.longitude
      ).as(SimplePhoto.simple *)
    }
  }

  def deletePhoto(photo: SimplePhoto): Unit = {
    println("delete: " + photo)
    db.withConnection { implicit con =>
      SQL("delete from simple_photos where id = {id}").on('id -> photo.id.get).executeUpdate()
      SQL("delete from tags where photo_id = {id}").on('id -> photo.id.get).executeUpdate()
    }
  }

  def deleteAllPhotosHasNoDateTime(): Unit = {
    val photos = db.withConnection { implicit con =>
      SQL("select * from simple_photos where date_time = ''").as(SimplePhoto.simple *)
    }
    photos.map(deletePhoto)
  }


  def main(args: Array[String]) = {
    deleteAllPhotosHasNoDateTime()
    dietAllPhotos()
  }
}
