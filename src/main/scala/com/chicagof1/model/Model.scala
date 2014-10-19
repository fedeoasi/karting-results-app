package com.chicagof1.model

import org.joda.time.{LocalTime, LocalDate, Duration}

case class RacerResult(name: String, position: Int, kart: Int, time: Duration, penalty: Option[Penalty] = None) {
  def formattedTime: String = {
    if(time.getMillis == 0) {
      "-"
    } else {
      time.getStandardSeconds + ":%03d".format(time.getMillis % 1000)
    }
  }

  def applyPenalty(points: Int): Int = penalty.map(_.compute(points)).getOrElse(points)
}
case class EditionResult(name: String, kart: Int, time: Duration)

case class Race(date: LocalDate, time: LocalTime, results: Seq[RacerResult]) extends HasRacerResults {
  def raceId: String = s"${date.toString}-${time.toString("hh:mm")}"
}

trait Penalty {
  def compute(points: Int): Int
}

class HalfPointsPenalty extends Penalty {
  override def compute(points: Int): Int = {
    if (points % 2 == 0) points / 2 else points / 2 + 1
  }
}

case class Edition(date: LocalDate, results: Seq[RacerResult]) extends HasRacerResults {
  def printId: String = date.toString("MMM yyyy")
}

trait HasRacerResults {
  def winner: String = results(0).name
  def results: Seq[RacerResult]
}

case class Video(id: String, racer: String, date: String, location: String)

case class EditionWithRaces(edition: Edition, races: Seq[Race])

case class RacerDao(id: Int, name: String, aliases: List[String], flag: String)
case class Racer(id: Int, name: String, flag: String)
