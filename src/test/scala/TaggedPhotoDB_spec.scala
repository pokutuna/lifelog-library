package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import anorm._

class TaggedPhotoDBSpec extends SpecHelper {
  val db = new TaggedPhotoDB("src/test/resources/test_taggedphoto.db")

  val tag1 = new Tag(1, "hoge", "bt")
  val tag2 = new Tag(1, "fuga", "bt")
  val tag3 = new Tag(2, "piyo", "wf")
  val tag4 = new Tag(3, "piyo", "wf")
  val tag5 = new Tag(4, "piyo", "wf")

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

  describe("Tag") {

    it("should insert Tag") {
      db.insertTag(tag1)
      val idTag = tag1.copy(id = Id(1))
      db.findTag(idTag) should be (Some(idTag))
    }

    it("should insert Tags") {
      insertTags()
      db.findTagByPhotoId(1).toSet should be (Set(tag1.copy(id = Id(1)), tag2.copy(id = Id(2))))
      db.findTagByPhotoId(2, "piyo").toList should be (List(tag3.copy(id = Id(3))))
    }

    it("should find by address") {
      insertTags()
      db.findTagByAddress("piyo").map(_.photoId).toSet should be (Set(2, 3, 4))
      db.findTagByAddress("piyo", offset = 1, limit = 1).map(_.photoId) should be (List(3))
    }

    it("should find by photo id") {
      insertTags()
      db.findTagByPhotoId(2).map(_.address).toList should be (List("piyo"))
      db.findTagByPhotoId(1, "hoge").map(_.deviceType).toList should be (List("bt"))
    }

    it("should count tags by photo id") {
      insertTags()
      db.countTagByPhotoId(1) should be (2)
      db.countTagByPhotoId(3) should be (1)
      db.countTagByPhotoId(-1) should be (0)
    }

    it("should count tags by address") {
      insertTags()
      db.countTagByAddress("piyo") should be (3)
      db.countTagByAddress("hoge") should be (1)
      db.countTagByAddress("1000000") should be (0)
    }
  }

  describe("SimplePhoto") {

    it("should insert photo") {
      db.insertPhoto(photo1)
      val idPhoto = photo1.copy(id = Id(1))
      db.findPhoto(idPhoto) should be (Some(idPhoto))
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

  }

  describe("Join Table") {
    it("should find tag by photo date time") {
      insertPhotos()
      insertTags()
      val tags = db.findTagByPhotoDateTime("2011-11-25 00:00:00", "2011-11-25 00:01:00")
      tags.map(_.address).toList should be (List("hoge", "fuga", "piyo"))
    }

    it("should find tag by photo date time with limit and offset") {
      insertPhotos()
      insertTags()
      val tags = db.findTagByPhotoDateTime("2011-11-25 00:00:00", "2011-11-25 00:01:00", offset = 2, limit = 100)
      tags.map(_.address).toList should be (List("piyo"))
    }
  }

}
