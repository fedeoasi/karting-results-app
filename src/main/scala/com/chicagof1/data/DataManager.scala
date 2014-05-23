package com.chicagof1.data

import com.chicagof1.model._
import com.chicagof1.ResultsImporter
import grizzled.slf4j.Logging
import com.chicagof1.parsing.VideoDeserializer
import org.joda.time.LocalDate
import com.chicagof1.model.RacerResult
import com.chicagof1.model.Edition
import com.chicagof1.model.Video
import com.chicagof1.model.Race
import com.chicagof1.utils.{FileUtils, DateUtils}
import FileUtils._

object DataProvider extends Logging {
  lazy val vd = new VideoDeserializer

  def dataManager(): DataManager = {
    val racers = loadRacers()
    val races = loadRaces()
    val editions = loadEditions()
    val videos = loadVideos()

    val racerResults =
      races
        .map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv")))
        .map(r => Race(r.date, r.time, r.results.map {
          rr =>
            val racer = racers.get(rr.name) match {
              case Some(racerName) => racerName
              case None => rr.name
            }
            RacerResult(racer, rr.position, rr.kart, rr.time)
          }))
        .sortBy(r => r.raceId).reverse.toList

    val editionResults: List[Edition] =
      editions.map(name =>
        ResultsImporter.readEdition(name, loadFileIntoString("edition/" + name + ".csv")))
        .map(e => {
           Edition(e.date, e.results.filter(r =>
             racers.contains(r.name))
             .zipWithIndex
             .map {
              case (rr, index) =>
                RacerResult(racers(rr.name), index + 1, rr.kart, rr.time)
              })
         })
        .sortBy(_.date.toString)
        .reverse
        .toList
    info(s"Imported ${races.size} races and ${editions.size} editions")
    new DataManager(racerResults, editionResults, videos)
  }

  private def loadRacers(): Map[String, String] = {
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

  private def loadRaces(): Seq[String] = loadStringsFromFiles("races.txt", "extraRaces.txt")

  private def loadEditions(): Seq[String] = loadStringsFromFiles("editions.txt", "extraEditions.txt")

  private def loadVideos(): List[Video] = {
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

  def getRaceById(id: String): Option[Race] = racesMap.get(id)
  def getEditionById(id: String): Option[Edition] = editionsMap.get(id)

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
          .filter(e => m.contains(e.date.toDateTimeAtStartOfDay))
          .headOption
        val name = m.getStart.toString("MMM")
        edOpt match {
          case Some(ed) => ReportedEditionInChampionship(i + 1, name, ed)
          case None => NonReportedEditionInChampionship(i + 1, name)
        }
      }
    }
    Championship(name, champEditions, new ChicagoF1PointsSystem(months.size))
  }
}