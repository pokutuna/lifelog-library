package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.util._
import com.pokutuna.lifelog.util.FileSelector._

class FileSelectorSpec extends SpecHelper {
  describe("FileSelector") {
    it("should select files from path") {
      val files = FileSelector.select("lib/").map(_.getPath)
      files should be (Seq("lib/metadata-extractor-2.5.0-RC2.jar", "lib/xmpcore.jar"))
    }

    it("should select files with filter") {
      val files = FileSelector.select("src/test/resources", ".*\\.tsv".r).map(_.getName)
      files should be (Seq("test_btlogdata.tsv", "test_wifilogdata.tsv"))
    }

    it("should throw exception when was given not a directory") {
      evaluating{ FileSelector.select("src/test/resources/test_btlogdata.tsv") } should produce [IllegalArgumentException]
    }
  }
}
