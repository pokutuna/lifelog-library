package com.pokutuna.lifelog.test

import com.pokutuna.lifelog.util._

class DateTimeSpec extends SpecHelper {
  describe("DateTime") {
    it("should construct from some types") {
      DateTime.format("2011-7-1 1:9:5").asString should be ("2011-07-01 01:09:05")
      DateTime(2011,1,1,1,1,1).asString should be ("2011-01-01 01:01:01")
      DateTime(TimeUtil.parse("2011-7-7 1:8:05")).asString should be ("2011-07-07 01:08:05")
    }

    it("should get each fields") {
      val a = DateTime.format("2011-2-3 4:5:6")
      a.year should be === 2011
      a.month should be === 2
      a.day should be === 3
      a.hour should be === 4
      a.minute should be === 5
      a.second should be === 6
    }

    it("should get DateTime instance relatively") {
      val a = DateTime.format("2011-2-3 4:5:6")
      a.fromNow(day = 3).asString should be ("2011-02-06 04:05:06")
      a.fromNow(minute = 3).asString should be ("2011-02-03 04:08:06")
      a.fromNow(day = -2).asString should be ("2011-02-01 04:05:06")

      a.ago(second = 30).asString should be ("2011-02-03 04:04:36")
      a.ago(second = -30).asString should be ("2011-02-03 04:05:36")
      a.ago(year = 10).asString should be ("2001-02-03 04:05:06")
    }
  }

  describe("Implicits") {
    import com.pokutuna.lifelog.util.DateTime.Implicit._
    it("should convert to DateTime from String") {
      "2011-2-3 4:5:6".year should be === 2011
      "2011-2-3 4:5:6".second should be === 6
      "2011-2-3 4:5:6".hour should be === 4
    }

    it("should convert to String from DateTime") {
      def echoString(str: String): String = str
      echoString(DateTime.format("2011-2-3 4:5:6")) should be ("2011-02-03 04:05:06")
    }
  }
}
