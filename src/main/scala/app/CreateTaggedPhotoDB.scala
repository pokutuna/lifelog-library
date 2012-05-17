package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util._
import scala.annotation.tailrec

object CreateTaggedPhotoDB {

  class DateDeviceCache(sensing: SensingDB) {

    var cachedDate: String = ""
    var btDevices: Seq[BtDevice] = List()
    var wifiDevices: Seq[WifiDevice] = List()

    def getBt(date: String): Seq[BtDevice] = {
      if(cachedDate != date) update(date)
      return btDevices
    }

    def getWifi(date: String): Seq[WifiDevice] = {
      if(cachedDate != date) update(date)
      return wifiDevices
    }

    private def update(date: String) = {
      val bts = sensing.btSearchDatePrefixUniqueDevice(date)
      val wfs = sensing.wifiSearchDatePrefixUniqueDevice(date)
      this.cachedDate = date
      this.btDevices = bts
      this.wifiDevices = wfs

      println("cache update: " + date + "(" + bts.size + ", " + wfs.size + ")")
    }

  }

  class TaggedPhotoDBManager(path: String, sensing: SensingDB) {
    val db = new TaggedPhotoDB(path)

    db.applySchema()
    val cache = new DateDeviceCache(sensing)

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

    def calcDiffSecs(photo: SimplePhoto, devices: Seq[Device]): Seq[Int] = {
      try {
        println("calc diffSecs: " + photo.filename)
        require(photo.dateTime != "")
        sensing.withConnection { implicit connection =>
          devices.map { dev =>
            dev.deviceType match {
              case "bt" => BtDetected.calcNearestDetectionDiffSec(photo.dateTime, dev.address).get
              case "wf" => WifiDetected.calcNearestDetectionDiffSec(photo.dateTime, dev.address).get
              case _    => throw new RuntimeException("unvalid device type")
            }
          }
        }
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

    def createTags(photo: SimplePhoto, devices: Seq[(Int, Int)]): Seq[Tag] = {
      println("creating tag: " + photo.filename)
      db.withConnection { implicit connection =>
        devices.map { tup =>
          val (deviceId, diffSec) = tup
          new Tag(deviceId, photo.id.get, diffSec)
        }
      }
    }

    def getRelatedDevices(photo: SimplePhoto): Seq[Device] = {
      try {
        require(photo.dateTime != "")
        val date = DateTime.format(photo.dateTime).date
        (cache.getBt(date) ++ cache.getBt(date)).map(_.toDevice)
      } catch {
        case e =>
          println(photo.filename + " doesn't have dateTime field")
          List()
      }
    }

    def createNoLevelTags(photo: SimplePhoto): Seq[Tag] = {
      val devices = getRelatedDevices(photo)
      val deviceIds = db.insertDevice(devices)
      val deviceDiffSecs = calcDiffSecs(photo, devices)
      createTags(photo, deviceIds.zip(deviceDiffSecs))
    }

    def getDevicesByRange(photo: SimplePhoto, range: Int): Seq[Device] = {
      try {
        require(photo.dateTime != "")
        val start = DateTime.format(photo.dateTime).ago(second = range).asString
        val end = DateTime.format(photo.dateTime).fromNow(second = range).asString
        (sensing.btDetectedIn(start, end) ++ sensing.wifiDetectedIn(start, end)).map(_.toDevice).distinct
      } catch {
        case e => println("error: " + photo.filename); List()
      }
    }

    def createPresetLevelTags(photo: SimplePhoto): Seq[Tag] = {
      // range 3min, 15min, 30min, 1h, 3h, 6h
      val presets = List(3 * 60)//, 15 * 60, 30 * 60, 60 * 60, 3 * 60 * 60, 6 * 60 * 60)
      val tags = presets.flatMap{ pre =>
        val devices = getDevicesByRange(photo, pre)
        val deviceIds = db.insertDevice(devices)
        createTags(photo, deviceIds.map((_, pre)))
      }
      return tags
    }

    def generateFromPhoto(photo: SimplePhoto) {
      //val tags = createNoLevelTags(photo)
      val tags = createPresetLevelTags(photo)
      db.insertTag(tags)
    }

    def insertTagsAndDevices() = {
      @tailrec
      def insertTagsRec(sensing: SensingDB, offset: Int, limit: Int): Unit = {
        println("inserting tags: " + offset)
        val photos = db.photo(offset, limit)
        if (photos.isEmpty)
          return
        else {
          photos.foreach(generateFromPhoto(_))
          insertTagsRec(sensing, offset + limit, limit)
        }
      }
      insertTagsRec(sensing, 0, 1000)
    }
  }

  def run(sensing: Option[String], lifelog: Option[String], dbPath: Option[String]) = {
    val sensingDB = new SensingDB(sensing.getOrElse("sensing.db"))
    val lifelogDB = new LifelogDB(lifelog.getOrElse("lifelog.db"))

    val dbm = new TaggedPhotoDBManager(dbPath.getOrElse("tagged_photo.db"), sensingDB)
    dbm.copyPhotos(lifelogDB)
    dbm.insertTagsAndDevices()
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
