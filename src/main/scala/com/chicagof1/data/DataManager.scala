package com.chicagof1.data

import com.chicagof1.ResultsImporter
import grizzled.slf4j.Logging
import com.chicagof1.parsing.VideoDeserializer
import org.joda.time.LocalDate
import com.chicagof1.model._
import com.chicagof1.utils.{FileUtils, DateUtils}
import FileUtils._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object DataProvider extends Logging {
  lazy val vd = new VideoDeserializer

  def dataManager(): DataManager = {
    val getDataManager = for {
      racerMap <- loadRacerAliasMap()
      raceNames <- loadRaceNames()
      editions <- loadEditionNames()
      videos <- loadVideos()
    } yield new DataManager(
        extractRaces(raceNames, racerMap),
        extractEditions(racerMap, editions),
        videos
      )
    Await.result(getDataManager, 60 seconds)
  }

  def extractEditions(racers: Map[String, String], editions: Seq[String]): List[Edition] = {
    val editionResults: List[Edition] =
      editions.par.map(name =>
        ResultsImporter.readEdition(name, loadFileIntoString("edition/" + name + ".csv")))
        .map(e => {
        Edition(e.date, e.results.filter(r =>
          racers.contains(r.name))
          .zipWithIndex
          .map {
          case (rr, index) =>
            RacerResult(racers(rr.name), index + 1, rr.kart, rr.time)
        })
      }).toList
        .sortBy(_.date.toString)
        .reverse
    editionResults
  }

  def extractRaces(races: Seq[String], racerAliases: Map[String, String]): List[Race] = {
    val racerResults =
      races.par
        .map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv")))
        .map(r => Race(r.date, r.time, r.results.map {
        rr =>
          val racer = racerAliases.get(rr.name) match {
            case Some(racerName) => racerName
            case None => rr.name
          }
          RacerResult(racer, rr.position, rr.kart, rr.time)
      })).toList
        .sortBy(r => r.raceId).reverse.toList
    racerResults
  }

  private def loadRacerAliasMap(): Future[Map[String, String]] = Future {
    var racerMap = Map.empty[String, String]
    val lines = loadStringsFromFiles("racers.txt")
    lines.foreach {
      l =>
        val split = l.split(",")
        val primaryName = split(0)
        split.foreach {
          n => racerMap = racerMap.updated(n, primaryName)
        }
    }
    racerMap
  }

  private def loadRaceNames(): Future[Seq[String]] = Future {
    loadStringsFromFiles("races.txt", "extraRaces.txt")
  }

  private def loadEditionNames(): Future[Seq[String]] = Future {
    loadStringsFromFiles("editions.txt", "extraEditions.txt")
  }

  private def loadVideos(): Future[List[Video]] = Future {
    try {
      val json = loadFileIntoString("videos.json")
      vd.deserializeVideos(json).toList
    } catch {
      case t: Throwable => {
        logger.error("Could not load videos", t)
        List.empty[Video]
      }
    }
  }
}

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