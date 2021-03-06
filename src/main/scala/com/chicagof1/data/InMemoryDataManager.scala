package com.chicagof1.data

import org.joda.time.LocalDate
import com.chicagof1.model._
import com.chicagof1.utils.DateUtils
import scala.language.postfixOps
import com.chicagof1.links.LinkBuilder
import scala.collection.immutable.SortedMap
import grizzled.slf4j.Logging

trait DataManager {
  def racers: List[SingleRacer]
  def teams: List[Team]
  def getRacerById(id: Int): Option[SingleRacer]
  def getRacerByName(id: String): Option[SingleRacer]
  def getRaceById(id: String): Option[Race]
  def getEditionById(id: String): Option[Edition]
  def getEditionWithRacesById(id: String): Option[EditionWithRaces]
  def currentChampionship: Championship
  def championship(id: String): Option[Championship]
  def championships: Seq[Championship]
  def buildMonthlyChampionship(name: String, start: LocalDate, stop: LocalDate, pointsSystem: PointsSystem): Championship
  def buildEditionsWithRaces(): List[EditionWithRaces]
  def racerStatsFor(name: String): RacerWithStats
  def racerLink(name: String): String
  def videos: List[Video]
  def editionsWithRaces: List[EditionWithRaces]
  def reload(): Unit
}

case class InMemoryDataManager(optionalData: Option[ChicagoF1Data] = None) extends DataManager with Logging {
  private var data: ChicagoF1Data = optionalData.getOrElse(DataProvider.loadData())
  private val sc = new StatsCalculator
  private val racersById: Map[Int, SingleRacer] = data.racers.map(r => r.id -> r).toMap
  private val racersByName: Map[String, SingleRacer] = data.racers.map(r => r.name -> r).toMap
  private val racesMap: Map[String, Race] = data.races.map(r => r.raceId -> r).toMap
  private val editionsMap: Map[String, Edition] = data.editions.map(e => e.date.toString -> e).toMap
  val editionsWithRaces: List[EditionWithRaces] = buildEditionsWithRaces()
  val editionWithRacesMap: Map[String, EditionWithRaces] =
    editionsWithRaces.map(er => er.edition.date.toString -> er).toMap

  override def getRacerById(id: Int): Option[SingleRacer] = racersById.get(id)
  override def getRacerByName(id: String): Option[SingleRacer] = racersByName.get(id)
  override def getRaceById(id: String): Option[Race] = racesMap.get(id)
  override def getEditionById(id: String): Option[Edition] = editionsMap.get(id)
  override def getEditionWithRacesById(id: String): Option[EditionWithRaces] = editionWithRacesMap.get(id)

  lazy val championshipMap: Map[String, Championship] = {
    val championship2014 = buildMonthlyChampionship(
      "2014",
      LocalDate.parse("2014-01-01"),
      LocalDate.parse("2014-11-30"),
      new ChicagoF12014PointsSystem)
    val championship2015 = buildMonthlyChampionship(
      "2015",
      LocalDate.parse("2015-01-01"),
      LocalDate.parse("2015-11-30"),
    new ChicagoF12015PointsSystem)
    val championship2016 = buildMonthlyChampionship(
      "2016",
      LocalDate.parse("2016-03-01"),
      LocalDate.parse("2016-11-30"),
      new ChicagoF12015PointsSystem)
    val championship2019 = buildMonthlyChampionship(
      "2019",
      LocalDate.parse("2019-05-01"),
      LocalDate.parse("2019-10-31"),
      new FormulaOnePointsSystem)
    val tuples = Seq(championship2014, championship2015, championship2016, championship2019).map { c => c.id -> c}
    SortedMap(tuples: _*)
  }

  override def championship(id: String): Option[Championship] = championshipMap.get(id)

  override def championships: Seq[Championship] = championshipMap.values.toSeq

  lazy val currentChampionship: Championship = {
    championshipMap("2019")
  }

  override def buildMonthlyChampionship(id: String, start: LocalDate, stop: LocalDate, pointsSystem: PointsSystem): Championship = {
    val months = DateUtils.monthsBetween(start.toDateTimeAtStartOfDay, stop.toDateTimeAtStartOfDay)
    val champEditions = months.zipWithIndex.map {
      case (m, i) =>
        val edOpt = data.editions.find(e => m.contains(e.date.toDateTimeAtStartOfDay))
        val name = m.getStart.toString("MMM")
        edOpt match {
          case Some(ed) => ReportedEditionInChampionship(i + 1, name, ed)
          case None => NonReportedEditionInChampionship(i + 1, name)
        }
    }
    Championship(id, champEditions, pointsSystem, teams)
  }

  override def buildEditionsWithRaces(): List[EditionWithRaces] = {
    val racesByDate = data.races.groupBy(_.date)
    data.editions.map(e => EditionWithRaces(e, racesByDate.getOrElse(e.date, List())))
  }

  override def racerStatsFor(name: String): RacerWithStats = {
    getRacerByName(name) match {
      case Some(r) =>
        sc.racerStatsFor(r, data.races, data.editions, data.videos, currentChampionship.standings)
      case None => throw new IllegalArgumentException(s"No racer named: $name")
    }
  }

  override def racerLink(name: String): String = LinkBuilder.racerLink(name, racersByName.get(name))

  override def videos: List[Video] = data.videos

  override def reload(): Unit = {
    data = DataProvider.loadData()
  }

  override def racers: List[SingleRacer] = data.racers

  override def teams: List[Team] = data.teams
}
