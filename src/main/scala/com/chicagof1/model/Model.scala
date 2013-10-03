package com.chicagof1.model

import org.joda.time.{LocalTime, LocalDate, Duration}

case class RacerResult(name: String, position: Int, kart: Int, time: Duration) {
  def formattedTime = time.getStandardSeconds + ":%03d".format(time.getMillis % 1000)
}
case class EditionResult(name: String, kart: Int, time: Duration)

case class Race(date: LocalDate, time: LocalTime, results: Seq[RacerResult]) {
  def raceId: String = {
    date.toString + "-" + time.toString("hh:mm")
  }
}

case class Edition(date: LocalDate, results: Seq[RacerResult])