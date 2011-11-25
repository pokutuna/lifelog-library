package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._

class LifelogDBSpec extends SpecHelper {

  val db = new LifelogDB("src/test/resources/test_lifelog.db")

  val photo1 = PhotoRecord("dir1", "fn1", "2011-07-07 00:00:00", 35.0, 135.0, 10, 10, 10, 2011, 7, 7, 0, 0, 0, "hoge1")
  val photo2 = PhotoRecord("dir2", "fn2", "2011-07-07 00:01:00", 36.0, 136.0, 10, 10, 10, 2011, 7, 7, 0, 1, 0, "hoge2")
  val photo3 = PhotoRecord("dir3", "fn3", "2011-07-07 00:02:00", 36.5, 135.0, 10, 10, 10, 2011, 7, 7, 0, 2, 0, "hoge3")
  val photo4 = PhotoRecord("dir4", "fn3", "2011-07-07 00:03:00", 34.5, 134.5, 10, 10, 10, 2011, 7, 7, 0, 3, 0, "hoge4")

  def cleanDB = {
    import scala.util.control.Exception._
    allCatch.opt(db.applySchema)
  }

  def insertExamples = {
    List(photo1, photo2, photo3, photo4).foreach(p => db.insertPhoto(p))
  }

  override def beforeEach = {
    cleanDB
    insertExamples
  }

  describe("Photo Taken In") {
    it("should get PhotoRecord") {
      val records1 = db.photoTakenIn("2011-07-07 00:00:00", "2011-07-07 00:00:00").toList
      records1 should be (List(photo1))

      val records2 = db.photoTakenIn("2011-07-07 00:00:30", "2011-07-07 00:02:00").toList
      records2 should be (List(photo2, photo3))

      val records3 = db.photoTakenIn("2011-07-08 00:00:00", "2011-07-07 00:00:00").toList
      records3 should be (Nil)
    }
  }

  describe("Photo Taken Where") {
    it("should get PhotoRecord") {
      val records1 = db.photoTakenWhere(34.0, 134.0, 0.5).toSet
      records1 should be (Set(photo4))

      val records2 = db.photoTakenWhere(0.0, 0.0, 0.0).toSet
      records2 should be (Set())

      val records3 = db.photoTakenWhere(35.0, 134.5, 0.5).toSet
      records3 should be (Set(photo1, photo4))
    }
  }

  describe("Photo Taken By Name") {
    it("should get PhotoRecord") {
      val records1 = db.photoByName("fn3").toSet
      records1 should be (Set(photo3, photo4))

      val records2 = db.photoByName("dir3", "fn3").toSet
      records2 should be (Set(photo3))
    }
  }

  describe("Other") {
    it("should check file existence") {
      db.existsFile("dir1", "fn1") should be (true)
      db.existsFile("dir2", "fn3") should be (false)
      db.existsFile("dir4") should be (false)
      db.existsFile("fn3") should be (true)
    }
  }

  describe("Insert Photo Records") {
    it("should insert PhotoRecord") {
      val photo = PhotoRecord("dir5", "fn5", "hogedate", 35.0, 135.0, 10, 10, 10, 2011, 7, 7, 0, 0, 0, "fuga")
      db.insertPhoto(photo)
      db.existsFile("dir5", "fn5") should be (true)

      db.insertPhoto(photo)
      db.photoTakenIn("hogedate", "hogedate").toList should be (List(photo, photo))
    }

  }

  describe("Paging Drop & Take") {
    it("should dorp & take") {
      val a = db.photoTakenIn("2011-07-07 00:00:00", "2011-07-08 00:00:00", 2, 1)
      a.toList should be (List(photo3))
      val b = db.photoTakenIn("2011-07-07 00:00:00", "2011-07-08 00:00:00", 10, 1)
      b.toList should be (List())
    }

    it("should take photos simply") {
      db.photo(offset = 1, limit = 2).toList should be (List(photo2, photo3))
      db.photo(offset = 3, limit = 100).toList should be (List(photo4))
      db.photo(offset = 100, limit = 100).toList should be (List())
    }
  }

}
