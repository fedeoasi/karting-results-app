package com.chicagof1.model

import org.joda.time.{LocalTime, LocalDate, Duration}

case class RacerResult(name: String, position: Int, kart: Int, time: Duration) {
  def formattedTime = {
    if(time.getMillis == 0) "-"
    else time.getStandardSeconds + ":%03d".format(time.getMillis % 1000)
  }
}
case class EditionResult(name: String, kart: Int, time: Duration)

case class Race(date: LocalDate, time: LocalTime, results: Seq[RacerResult]) {
  def raceId: String = s"${date.toString}-${time.toString("hh:mm")}"
}

case class Edition(date: LocalDate, results: Seq[RacerResult]) {
  def winner: String = results(0).name
  def printId: String = date.toString("MMM yyyy")
}

trait HasRacerResults {
  def results: Seq[RacerResult]
}

case class Video(id: String, racer: String, date: String, location: String)

case class EditionWithRaces(edition: Edition, races: Seq[Race])