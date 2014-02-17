package com.chicagof1.data

import com.chicagof1.model._
import com.chicagof1.ResultsImporter
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging
import com.chicagof1.parsing.VideoDeserializer
import org.joda.time.LocalDate
import com.chicagof1.model.RacerResult
import com.chicagof1.model.Edition
import com.chicagof1.model.Video
import com.chicagof1.model.Race
import com.chicagof1.utils.DateUtils

object DataProvider extends Logging {
  lazy val vd = new VideoDeserializer

  def dataManager(): DataManager = {
    val racers = loadRacers()
    val races = loadRaces()
    val editions = loadEditions()
    val videos = loadVideos()

    val racerResults =
      races.map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv")))
        .sortBy(r => r.raceId).reverse.toList

    val editionResults: List[Edition] =
      editions.map(name =>
        ResultsImporter.readEdition(name, loadFileIntoString("edition/" + name + ".csv")))
        .map(e => {
           Edition(e.date, e.results.filter(r =>
             racers.contains(r.name))
             .zipWithIndex
             .map(r => RacerResult(r._1.name, r._2 + 1, r._1.kart, r._1.time)))
         })
        .sortBy(_.date.toString)
        .reverse
        .toList
    info(s"Imported ${races.size} races and ${editions.size} editions")
    new DataManager(racerResults, editionResults, videos)
  }

  private def loadRacers(): Seq[String] = loadStringsFromFiles("racers.txt")

  private def loadRaces(): Seq[String] = loadStringsFromFiles("races.txt", "extraRaces.txt")

  private def loadEditions(): Seq[String] = loadStringsFromFiles("editions.txt", "extraEditions.txt")

  private def loadVideos(): List[Video] = {
    try {
      val json = loadFileIntoString("videos.json")
      vd.deserializeVideos(json).toList
    } catch {
      case t: Throwable => List.empty[Video]
    }
  }

  private def loadStringsFromFiles(filenames: String*): Seq[String] = {
    filenames.flatMap {
      case f => loadFileIntoString(f).split("\\n").filterNot(_.isEmpty)
    }
  }

  def loadFileIntoString(path: String): String = {
    debug("Opening resource at path: " + path)
    val racesStream = Thread.currentThread().getContextClassLoader.getResourceAsStream(path)
    if(racesStream == null) {
      error("Unable to stream resource at path: " + path)
    }
    val writer = new StringWriter()
    try {
      IOUtils.copy(racesStream, writer, "UTF-8")
      writer.toString
    } catch {
      case t: Throwable => {
        error("Error while processing resource: " + path.toString)
        println(t.printStackTrace())
      }
      ""
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