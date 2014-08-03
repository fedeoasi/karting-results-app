package com.chicagof1.data

import org.joda.time.LocalDate
import com.chicagof1.model._
import com.chicagof1.utils.DateUtils
import scala.language.postfixOps

case class DataManager(racers: List[Racer], races: List[Race], editions: List[Edition], videos: List[Video]) {
  private val sc = new StatsCalculator
  private val racersById: Map[Int, Racer] = racers.map(r => r.id -> r).toMap
  private val racersByName: Map[String, Racer] = racers.map(r => r.name -> r).toMap
  private val racesMap: Map[String, Race] = races.map(r => r.raceId -> r).toMap
  private val editionsMap: Map[String, Edition] = editions.map(e => e.date.toString -> e).toMap
  lazy val editionsWithRaces: List[EditionWithRaces] = buildEditionsWithRaces()
  lazy val editionWithRacesMap: Map[String, EditionWithRaces] =
    editionsWithRaces.map(er => er.edition.date.toString -> er).toMap

  def getRacerById(id: Int): Option[Racer] = racersById.get(id)
  def getRacerByName(id: String): Option[Racer] = racersByName.get(id)
  def getRaceById(id: String): Option[Race] = racesMap.get(id)
  def getEditionById(id: String): Option[Edition] = editionsMap.get(id)
  def getEditionWithRacesById(id: String): Option[EditionWithRaces] = editionWithRacesMap.get(id)

  def currentChampionship: Championship = {
    buildMonthlyChampionship(
      "Chicago F1 2014",
      LocalDate.parse("2014-01-01"),
      LocalDate.parse("2014-12-31"))
  }

  def buildMonthlyChampionship(name: String, start: LocalDate, stop: LocalDate): Championship = {
    val months = DateUtils.monthsBetween(start.toDateTimeAtStartOfDay, stop.toDateTimeAtStartOfDay)
    val champEditions = months.zipWithIndex.map {
      case (m, i) => {
        val edOpt = editions
          .find(e => m.contains(e.date.toDateTimeAtStartOfDay))
        val name = m.getStart.toString("MMM")
        edOpt match {
          case Some(ed) => ReportedEditionInChampionship(i + 1, name, ed)
          case None => NonReportedEditionInChampionship(i + 1, name)
        }
      }
    }
    Championship(name, champEditions, new ChicagoF1PointsSystem(months.size))
  }

  def buildEditionsWithRaces(): List[EditionWithRaces] = {
    val racesByDate = races.groupBy(_.date)
    editions.map(e => EditionWithRaces(e, racesByDate.getOrElse(e.date, List())))
  }

  def racerStatsFor(name: String): RacerWithStats = {
    getRacerByName(name) match {
      case Some(r) =>
        sc.racerStatsFor(r, races, editions, videos, currentChampionship.standings)
      case None => throw new IllegalArgumentException(s"No racer named: $name")
    }
  }
}