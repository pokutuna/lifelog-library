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

  def cleanDB() = {
    db.applySchema()
  }

  override def beforeEach = {
    cleanDB()
  }

  def insertTags() = {
    db.insertTag(List(tag1, tag2, tag3, tag4, tag5))
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

}
