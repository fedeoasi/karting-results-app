package com.chicagof1.data

import org.joda.time.LocalDate
import com.chicagof1.model._
import com.chicagof1.utils.{FileUtils, DateUtils}
import scala.language.postfixOps
import scala.async.Async.async

case class DataManager(races: List[Race], editions: List[Edition], videos: List[Video]) {
  private val racesMap: Map[String, Race] = races.map(r => r.raceId -> r).toMap
  private val editionsMap: Map[String, Edition] = editions.map(e => e.date.toString -> e).toMap
  lazy val editionsWithRaces: List[EditionWithRaces] = buildEditionsWithRaces()
  lazy val editionWithRacesMap: Map[String, EditionWithRaces] =
    editionsWithRaces.map(er => er.edition.date.toString -> er).toMap

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
}