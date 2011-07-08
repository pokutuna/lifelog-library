package com.pokutuna.Lifelog.test

import com.pokutuna.Lifelog.db.dao._
import com.pokutuna.Lifelog.db.model.LifelogModel._

class LifelogDAOSpec extends SpecHelper {

  val db = new LifelogDAO("jdbc:sqlite:src/test/resources/test_lifelog.db")

  val photo1 = PhotoRecord("dir1", "fn1", "2011-07-07 00:00:00", 35.0, 135.0, 10, 10, 10, 2011, 7, 7, 0, 0, 0)
  val photo2 = PhotoRecord("dir2", "fn2", "2011-07-07 00:01:00", 36.0, 136.0, 10, 10, 10, 2011, 7, 7, 0, 1, 0)
  val photo3 = PhotoRecord("dir3", "fn3", "2011-07-07 00:02:00", 35.5, 135.0, 10, 10, 10, 2011, 7, 7, 0, 2, 0)
  val photo4 = PhotoRecord("dir4", "fn3", "2011-07-07 00:03:00", 34.5, 134.5, 10, 10, 10, 2011, 7, 7, 0, 3, 0)

  def cleanDB = {
    import scala.util.control.Exception._
    allCatch.opt(db.dropAll)
    allCatch.opt(db.createAll)
  }

  def insertExamples = {
    List(photo1, photo2, photo3, photo4).foreach(p => db.insertPhotoRecord(p))
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
      val records1 = db.photoTakenWhere(34.0, 35.0, 134.0, 134.5).toSet
      records1 should be (Set(photo4))

      val records2 = db.photoTakenWhere(0.0, 0.0, 0.0, 0.0).toSet
      records2 should be (Set())

      val records3 = db.photoTakenWhere(0.0, 180.0, 134.0, 135.0).toSet
      records3 should be (Set(photo1, photo3, photo4))
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
      val photo = PhotoRecord("dir5", "fn5", "hogedate", 35.0, 135.0, 10, 10, 10, 2011, 7, 7, 0, 0, 0)
      db.insertPhotoRecord(photo)
      db.existsFile("dir5", "fn5") should be (true)

      db.insertPhotoRecord(photo)
      db.photoTakenIn("hogedate", "hogedate").toList should be (List(photo, photo))
    }
  }
}
