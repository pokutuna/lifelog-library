package com.pokutuna.lifelog.test

import anorm._
import com.pokutuna.lifelog.db.dao._

class SensingDBSpec extends SpecHelper {
  describe("SensingDB") {
    it("should read schema file") {
      val db = new SensingDB(":memory:")
      db.readSchema
    }

    it("should apply schema") {
      val db = new SensingDB("src/test/resources/test_sensing.db")
      db.applySchema
      db.withConnection { implicit conn =>
        SQL("insert into bt_detected values ({address}, {date_time}, {file_id});").on(
          'address -> "hoge", 'date_time -> "2011-11-15 15:16:00", 'file_id -> 1
        ).executeUpdate()
      }
    }
  }
}
