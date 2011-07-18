package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.db.model.PhotoRecordFactory
import java.io.File

class PhotoRecordFactorySpec extends SpecHelper {

  val tanuPhoto = new File("src/test/resources/tanu.jpg")
  val iconPhoto = new File("src/test/resources/icon.jpg")

  describe("PhotoRecordFactory") {
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
  }
}
