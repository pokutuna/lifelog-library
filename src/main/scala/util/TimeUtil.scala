package com.pokutuna.lifelog.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object TimeUtil {

  val SECOND = 1000
  val MINUTE = 60000
  val HOUR = MINUTE * 60
  val DAY = HOUR * 24

  def getDateFormat: SimpleDateFormat = {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  }

  def parseAndFormat(str: String): String = format(parse(str))

  def parse(str: String): Date = {
    val d = getDateFormat.parse(str)
    new Date(d.getTime)
  }

  def format(ts: Date): String = {
    getDateFormat.format(ts.getTime)
  }

  def dateToCalendar(ts: Date): Calendar = {
    val c = Calendar.getInstance
    c.setTimeInMillis(ts.getTime)
    return c
  }

  def splitToArray(date: Date): Array[Int] = {
    val cal = dateToCalendar(date)
    Array(
      cal.get(Calendar.YEAR),
      cal.get(Calendar.MONTH) + 1,
      cal.get(Calendar.DATE),
      cal.get(Calendar.HOUR_OF_DAY),
      cal.get(Calendar.MINUTE),
      cal.get(Calendar.SECOND)
    )
  }

  def year(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.YEAR)

  def month(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.MONTH) + 1

  def day(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.DATE)

  def hour(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.HOUR_OF_DAY)

  def minute(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.MINUTE)

  def second(ts: Date): Int =
    dateToCalendar(ts).get(Calendar.SECOND)

  def after(da: Date, year: Int = 0, month: Int = 0, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0): Date = synchronized {
    val c = Calendar.getInstance
    c.setTime(da)
    c.add(Calendar.YEAR, year)
    c.add(Calendar.MONTH, month)
    c.add(Calendar.DATE, day)
    c.add(Calendar.HOUR_OF_DAY, hour)
    c.add(Calendar.MINUTE, minute)
    c.add(Calendar.SECOND, second)
    c.getTime
  }

  def before(da: Date, year: Int = 0, month: Int = 0, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0): Date = synchronized {
    after(da, -year, -month, -day, -hour, -minute, -second)
  }

}
