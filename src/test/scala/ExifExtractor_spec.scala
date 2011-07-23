package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.util.ExifExtractor
import java.io.File

class ExifExtractorSpec extends SpecHelper {

  describe("ExifExtractor") {

    val tanuPhoto = new File("src/test/resources/tanu.jpg")
    it("should extract exif from picture") {
      val exif = ExifExtractor.extract(tanuPhoto)
      exif.date.toString should be ("Some(Wed May 18 23:08:11 JST 2011)")
      exif.latitude.get should be (34.82 plusOrMinus 0.1)
      exif.longitude.get should be (135.31 plusOrMinus 0.1)
    }

    val iconPhoto = new File("src/test/resources/icon.jpg")
    it("should extract exif as None") {
      val exif = ExifExtractor.extract(iconPhoto)
      exif.date should be (None)
      exif.latitude should be (None)
      exif.longitude should be (None)
    }

    val instagramPhoto = new File("src/test/resources/instagram.jpg")
    it("shoud extraft exif from instagram picture") {
      val exif = ExifExtractor.extract(instagramPhoto)
      exif.date should be (None)
      exif.latitude should be (None)
      exif.longitude should be (None)
    }

    val nonPictureFile = new File("src/test/resources/test_btlogdata.tsv")
    it("should extract exif from not picture file") {
      val exif = ExifExtractor.extract(nonPictureFile)
      exif.date should be (None)
      exif.latitude should be (None)
      exif.longitude should be (None)
    }

    val notExistFile = new File("src/test/resources/non-exist-file")
    it("should extract exif from not exist file") {
      val exif = ExifExtractor.extract(notExistFile)
      exif.date should be (None)
      exif.latitude should be (None)
      exif.longitude should be (None)
    }
  }
}
