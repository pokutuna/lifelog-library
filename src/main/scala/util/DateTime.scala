package com.pokutuna.lifelog.util

import java.util.Calendar
import java.util.Date

case class DateTime private(dateTime: String) {

  lazy val toDate: Date = TimeUtil.parse(dateTime)
  def asString: String = dateTime

  def year: Int = TimeUtil.year(toDate)
  def month: Int = TimeUtil.month(toDate)
  def day: Int = TimeUtil.day(toDate)
  def hour: Int = TimeUtil.hour(toDate)
  def minute: Int = TimeUtil.minute(toDate)
  def second: Int = TimeUtil.second(toDate)

  // private lazy val splited = DateTime.splitAtSpace(this.asString)
  def date: String = splitAtSpace(this.asString)._1
  def time: String = splitAtSpace(this.asString)._2

  def fromNow(year: Int = 0, month: Int = 0, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0): DateTime = DateTime(TimeUtil.after(toDate, year, month, day, hour, minute, second))
  def ago(year: Int = 0, month: Int = 0, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0): DateTime = DateTime(TimeUtil.before(toDate, year, month, day, hour, minute, second))

  def roundDayStart: DateTime = {
    val array = TimeUtil.splitToArray(this.toDate)
    return DateTime(array(0), array(1), array(2), 0, 0, 0)
  }

  def roundDayEnd: DateTime = {
    val array = TimeUtil.splitToArray(this.toDate)
    return DateTime(array(0), array(1), array(2), 23, 59, 59)
  }

  def diffSeconds(date: DateTime): Int = {
    DateTime.diffSeconds(this, date)
  }

  private def splitAtSpace(date: String): (String, String) = {
    date.split(" ") match {
      case Array(date, time) => (date, time)
        case _ => throw new RuntimeException
    }
  }
}


object DateTime {

  def format(date: String): DateTime = new DateTime(TimeUtil.parseAndFormat(date))

  def apply(date: Date): DateTime = new DateTime(TimeUtil.format(date))

  def apply(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime = {
    val c = Calendar.getInstance
    c.set(year, month - 1, day, hour, minute, second)
    apply(c.getTime())
  }

  def diffSeconds(standard: DateTime, other: DateTime): Int = {
    ((other.toDate.getTime - standard.toDate.getTime) / 1000).toInt
  }

  object Implicit {
    implicit def stringToDateTime(string: String): DateTime = DateTime.format(string)
    implicit def dateTimeToString(dateTime: DateTime): String = dateTime.asString
  }

}
