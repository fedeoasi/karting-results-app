package com.chicagof1.utils

import org.joda.time.LocalDate
import com.github.nscala_time.time.Imports._

object DateUtils {
  def month(date: String): Interval = {
    val current = LocalDate.parse(date)
    month(current)
  }

  def month(dateTime: DateTime): Interval = {
    month(dateTime.toLocalDate)
  }

  def month(date: LocalDate): Interval = {
    val bom = date.dayOfMonth().withMinimumValue()
    val nextBom = date.plusMonths(1).dayOfMonth().withMinimumValue()
    new Interval(bom.toDateTimeAtStartOfDay, nextBom.toDateTimeAtStartOfDay)
  }

  def monthsBetween(start: DateTime, stop: DateTime): Seq[Interval] = {
    val s = month(start)
    val e = month(stop)
    if(s.getStartMillis < e.getStartMillis) {
      Seq(s) ++ monthsBetween(nextMonth(start).getStart, stop)
    } else if(s.getStart == e.getStart) {
      Seq(s)
    } else {
      Seq()
    }
  }

  def dateTime(year: Int, month: Int, day: Int): DateTime = {
    new DateTime().withDate(year, month, day)
  }

  def nextMonth(dateTime: DateTime): Interval = {
    month(dateTime.plusMonths(1))
  }
}
