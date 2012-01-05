package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import anorm._

class TaggedPhotoDBSpec extends SpecHelper {
  val db = new TaggedPhotoDB("src/test/resources/test_taggedphoto.db")

  val tag1 = new Tag(1, 1)
  val tag2 = new Tag(2, 1)
  val tag3 = new Tag(3, 2)
  val tag4 = new Tag(3, 3)
  val tag5 = new Tag(3, 4)

  val dev1 = new Device("hoge", "bt")
  val dev2 = new Device("fuga", "bt")
  val dev3 = new Device("piyo", "wf")

  val photo1 = new SimplePhoto("dir1", "name1", "2011-11-25 00:00:00", 30, 120)
  val photo2 = new SimplePhoto("dir1", "name2", "2011-11-25 00:01:00", 30, 120)
  val photo3 = new SimplePhoto("dir2", "name1", "2011-11-25 00:02:00", 35, 125)
  val photo4 = new SimplePhoto("dir2", "name2", "2011-11-26 00:03:00", 25, 115)
  val photo5 = new SimplePhoto("dir2", "name3", "2011-11-26 00:04:00", 0, 0)

  def cleanDB() = {
    db.applySchema()
  }

  override def beforeEach = {
    cleanDB()
  }

  def insertTags() = {
    db.insertTag(List(tag1, tag2, tag3, tag4, tag5))
  }

  def insertPhotos() = {
    db.insertPhoto(List(photo1, photo2, photo3, photo4, photo5))
  }

  def insertDevices() = {
    db.insertDevice(List(dev1, dev2, dev3))
  }

  describe("Tag") {

    it("should insert Tag") {
      db.insertTag(tag1) should be (1)
      db.findTag(tag1) should be (Some(tag1.copy(id = Id(1))))
    }

    it("should insert Tags") {
      insertTags().toList should be (List(1, 2, 3, 4, 5))
      db.findTagByPhotoId(1).toSet should be (Set(tag1.copy(id = Id(1)), tag2.copy(id = Id(2))))
      db.findTagByPhotoId(3).toList should be (List(tag4.copy(id = Id(4))))
    }

    it("should find by address") {
      insertTags()
      insertDevices()
      db.findTagByAddress("piyo").map(_.photoId).toSet should be (Set(2, 3, 4))
      db.findTagByAddress("piyo", offset = 1, limit = 1).map(_.photoId) should be (List(3))
    }

    it("should find by photo id") {
      insertTags()
      db.findTagByPhotoId(2).map(_.deviceId).toList should be (List(3))
      db.findTagByPhotoId(-1) should be (Seq())
    }

    it("should count tags by photo id") {
      insertTags()
      db.countTagByPhotoId(1) should be (2)
      db.countTagByPhotoId(3) should be (1)
      db.countTagByPhotoId(-1) should be (0)
    }

    it("should count tags by device id") {
      insertTags()
      db.countTagByDeviceId(1) should be (1)
      db.countTagByDeviceId(3) should be (3)
      db.countTagByDeviceId(-1) should be (0)
    }

    it("should count tags by address") {
      insertTags()
      insertDevices()
      db.countTagByAddress("piyo") should be (3)
      db.countTagByAddress("hoge") should be (1)
      db.countTagByAddress("1000000") should be (0)
    }

    it("should find tag by id") {
      insertTags()
      db.findTagById(3) should be (Some(tag3.copy(id = Id(3))))
      db.findTagById(1) should be (Some(tag1.copy(id = Id(1))))
      db.findTagById(-1) should be (None)
    }

    it("should find tag by device id") {
      insertTags()
      db.findTagByDeviceId(1).map(_.photoId).toList should be (List(1))
      db.findTagByDeviceId(2).map(_.photoId).toList should be (List(1))
      db.findTagByDeviceId(3).map(_.photoId).toList should be (List(2, 3, 4))
      db.findTagByDeviceId(-1).map(_.photoId).toList should be (List())
    }
  }

  describe("Device") {

    it("should insert device") {
      insertDevices().toList should be (List(1, 2, 3))
      db.insertDevice(new Device("temp_device", "temp_type")) should be (4)
      insertDevices().toList should be (List(1, 2, 3))
      db.insertDevice(new Device("temp_device2", "hoge")) should be (5)
    }

    it("should find by id") {
      insertDevices()
      db.findDeviceById(1) should be (Some(dev1.copy(id = Id(1))))
      db.findDeviceById(-1) should be (None)
      db.findDeviceById(3) should be (Some(dev3.copy(id = Id(3))))
    }

    it("should find by address") {
      insertDevices()
      db.findDeviceByAddress("hoge") should be (Some(dev1.copy(id = Id(1))))
      db.findDeviceByAddress("fuga") should be (Some(dev2.copy(id = Id(2))))
      db.findDeviceByAddress("もげら") should be (None)
    }

    it("should check id when find device") {
      insertDevices()
      db.withConnection { implicit connection =>
        Device.findIgnoreId(dev1.copy(id = Id(3))) should be (Some(dev1.copy(id = Id(1))))
        Device.findWithId(dev1.copy(id = Id(3))) should be (None)
        Device.findWithId(dev2.copy(id = Id(2))) should be (Some(dev2.copy(id = Id(2))))
      }
    }

  }

  describe("SimplePhoto") {

    it("should insert photo") {
      db.insertPhoto(photo1) should be (1)
      db.findPhoto(photo1) should be (Some(photo1.copy(id = Id(1))))
      insertPhotos should be (List(2, 3, 4, 5, 6))
    }

    it("should return id after inserting") {
      db.insertPhoto(photo1) should be (1)
      db.insertPhoto(photo2) should be (2)
    }

    it("should insert photos") {
      db.insertPhoto(List(photo1, photo2, photo3))
      db.findPhoto(photo1) should be (Some(photo1.copy(id = Id(1))))
      db.findPhoto(photo2) should be (Some(photo2.copy(id = Id(2))))
      db.findPhoto(photo3) should be (Some(photo3.copy(id = Id(3))))
      db.findPhoto(photo4) should be (None)
    }

    it("should find photo by name") {
      insertPhotos()
      db.findPhotoByName("dir2", "name3").map(_.filename).toSet should be (Set(photo5.filename))
      db.findPhotoByName("name1").map(_.filename).toSet should be (Set(photo1, photo3).map(_.filename))
      db.findPhotoByName("piyo--n").toSet should be (Set())
    }

    it("should find photo by date time") {
      insertPhotos()
      val photos = db.findPhotoByDateTime("2011-11-25 00:00:00", "2011-11-26 00:03:00")
      photos.map(_.directory).toList should be (List("dir1", "dir1", "dir2", "dir2"))
      val photos2 = db.findPhotoByDateTime("2011-11-25 00:00:00", "2011-11-26 00:03:00", offset = 2, limit = 1)
      photos2.toList should be (List(photo3.copy(id = Id(3))))
    }

    it("should search photo date time by glob pattern") {
      insertPhotos()
      val photos = db.findPhotoByDatePrefix("2011-11-26 ")
      photos.map(_.filename).toList should be (List("name2", "name3"))
      val photos2 = db.findPhotoByDatePrefix("2011-11-25 ", 1, 2)
      photos2.map(_.filename).toList should be (List("name2", "name1"))
    }

    it("should find photo by location") {
      insertPhotos()
      val photos = db.findPhotoByLocation(30, 120, 0)
      photos.map(_.dateTime).toList should be (List("2011-11-25 00:00:00", "2011-11-25 00:01:00"))
      val photos2 = db.findPhotoByLocation(30, 120, 5, offset = 2, limit = 2)
      photos2.map(_.filename).toList should be (List("name1", "name2"))
    }

    it("should take photo simply") {
      insertPhotos()
      val photos = db.photo(2, 1)
      photos.map(_.dateTime).toList should be (List("2011-11-25 00:02:00"))
      val photos2 = db.photo(3, 2)
      photos2.map(_.dateTime).toList should be (List("2011-11-26 00:03:00", "2011-11-26 00:04:00"))
    }

    it("should get latest date") {
      insertPhotos()
      val latest = db.latestDate
      latest should be ("2011-11-26 00:04:00")
    }

    it("should get oldest date") {
      insertPhotos()
      val oldest = db.oldestDate
      oldest should be ("2011-11-25 00:00:00")
    }

  }

  describe("Join Table") {
    it("should find tag by photo date time") {
      insertPhotos()
      insertTags()
      val tags = db.findTagByPhotoDateTime("2011-11-25 00:00:00", "2011-11-25 00:01:00")
      tags.map(_.deviceId).toList should be (List(1, 2, 3))
    }

    it("should find tag by photo date time with limit and offset") {
      insertPhotos()
      insertTags()
      val tags = db.findTagByPhotoDateTime("2011-11-25 00:00:00", "2011-11-25 00:01:00", offset = 2, limit = 100)
      tags.map(_.deviceId).toList should be (List(3))
    }
  }

}
