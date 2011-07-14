package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.util.TimeUtil

import java.util.Date

class TimeUtilSpec extends SpecHelper {

  describe("Field getter") {
    it("should get month 1 origin") {
      val a = TimeUtil.parse("2011-06-15 00:00:00")
      TimeUtil.month(a) should be (6)
    }

    it("should get some fields") {
      val a = TimeUtil.parse("2011-06-15 12:34:56")
      TimeUtil.year(a) should be === 2011
      TimeUtil.month(a) should be === 6
      TimeUtil.day(a) should be === 15
      TimeUtil.hour(a) should be === 12
      TimeUtil.minute(a) should be === 34
      TimeUtil.second(a) should be === 56
    }
  }

  describe("Before / After") {
    it("should calc before time") {
      val a = TimeUtil.parse("2011-06-15 12:34:56")
      val b = TimeUtil.before(a, year = 1, month = 6, minute = 35)
      b should be (TimeUtil.parse("2009-12-15 11:59:56"))
    }
  }

  describe("Parse / Format") {
    it("should parse and format time") {
      TimeUtil.parseAndFormat("2010-1-7 1:03:10") should be ("2010-01-07 01:03:10")
    }
  }
}
