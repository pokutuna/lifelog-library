package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.util.ExifExtractor
import java.io.File

class ExifExtractorSpec extends SpecHelper {

  describe("ExifExtractor") {
    val tanuPhoto = new File("src/test/resources/tanu.jpg")
    val iconPhoto = new File("src/test/resources/icon.jpg")

    it("should extract exif from picture") {
      val exif = ExifExtractor.extract(tanuPhoto)
      exif.date.toString should be ("Some(Wed May 18 23:08:11 JST 2011)")
      exif.latitude.get should be (34.82 plusOrMinus 0.1)
      exif.longitude.get should be (135.31 plusOrMinus 0.1)
    }

    it("should extract exif as None") {
      val exif = ExifExtractor.extract(iconPhoto)
      exif.date should be (None)
      exif.latitude should be (None)
      exif.longitude should be (None)
    }
  }
}
