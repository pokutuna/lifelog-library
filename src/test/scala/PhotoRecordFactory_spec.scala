package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.db.util.PhotoRecordFactory
import java.io.File

class PhotoRecordFactorySpec extends SpecHelper {

  describe("PhotoRecordFactory") {
    val tanuPhoto = new File("src/test/resources/tanu.jpg")
    it("should create PhotoRecord from File") {
      val record = PhotoRecordFactory(tanuPhoto)
      record.directory should be ("src/test/resources")
      record.filename should be ("tanu.jpg")
      record.orgDate should be ("2011-05-18 23:08:11")
      record.latitude should be (34.82 plusOrMinus 0.1)
      record.longitude should be (135.31 plusOrMinus 0.1)
      record.width should be === 640
      record.height should be === 478
      record.year should be === 2011
      record.month should be === 5
      record.day should be === 18
      record.hour should be === 23
      record.minute should be === 8
      record.second should be === 11
    }

    val iconPhoto = new File("src/test/resources/icon.jpg")
    it("should create PhotoRecord for non-Exif picture") {
      val record = PhotoRecordFactory("hoge", iconPhoto)
      record.directory should be ("hoge")
      record.filename should be ("icon.jpg")
      record.orgDate should be ("")
      record.latitude should be (0.0)
      record.longitude should be (0.0)
      record.width should be === 162
      record.height should be === 162
      record.year should be === 0
      record.day should be === 0
      record.minute should be === 0
    }

    val instagramPhoto = new File("src/test/resources/instagram.jpg")
    it("should create PhotoRecord for pictures for Instagram") {
      //NOTE: In extracting exif from instagram picture, it returns null.
      val record = PhotoRecordFactory(instagramPhoto)
      record.directory should be ("src/test/resources")
      record.filename should be ("instagram.jpg")
      record.orgDate should be ("")
      record.latitude should be (0.0)
      record.longitude should be (0.0)
      record.year should be === 0
      record.day should be === 0
      record.minute should be === 0
    }

    val nonPictureFile = new File("src/test/resources/test_btlogdata.tsv")
    it("should create PhotoRecord from a file is not picture") {
      val record = PhotoRecordFactory(nonPictureFile)
      record.directory should be ("src/test/resources")
      record.filename should be ("test_btlogdata.tsv")
      record.orgDate should be ("")
      record.latitude should be (0.0)
      record.longitude should be (0.0)
      record.year should be === 0
      record.day should be === 0
      record.minute should be === 0
    }

    val notExistFile = new File("src/test/resources/non-exist-file")
    it("should create PhotoRecord from a file is not exist") {
      evaluating { PhotoRecordFactory(notExistFile) } should produce [javax.imageio.IIOException]
    }
  }

  describe("PhotoRecord transform to SimplePhoto") {
    val tanuPhoto = new File("src/test/resources/tanu.jpg")
    it("should create PhotoRecord from File") {
      val record = PhotoRecordFactory(tanuPhoto).toSimplePhoto
      val sphoto = new SimplePhoto("src/test/resources", "tanu.jpg", "2011-05-18 23:08:11", 34.82, 135.31)

      record.directory should be (sphoto.directory)
      record.filename should be (sphoto.filename)
      record.dateTime should be (sphoto.dateTime)
      record.latitude should be (sphoto.latitude plusOrMinus 0.1)
      record.longitude should be (sphoto.longitude plusOrMinus 0.1)
    }
  }
}
