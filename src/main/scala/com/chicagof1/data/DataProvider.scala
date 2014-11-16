package com.chicagof1.data

import com.chicagof1.ResultsImporter
import grizzled.slf4j.Logging
import com.chicagof1.parsing.{TeamDeserializer, RacerDeserializer, VideoDeserializer}
import com.chicagof1.model._
import com.chicagof1.utils.FileUtils
import FileUtils._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.async.Async.{async, await}

class ChicagoF1Data(val racers: List[SingleRacer], val teams: List[Team], val races: List[Race], val editions: List[Edition], val videos: List[Video])

object DataProvider extends Logging {
  lazy val vd = new VideoDeserializer
  lazy val td = new TeamDeserializer
  lazy val rd = new RacerDeserializer
  val oneMillion = 1000000

  def loadData(): ChicagoF1Data = {
    val start = System.nanoTime()
    val asyncDataManager = async {
      val videosFuture = Future { loadVideos() }
      val teamsFuture = Future { loadTeams() }
      val racerDaosFuture = Future { loadRacers() }
      val raceNamesFuture = Future { loadRaceNames() }
      val editionNamesFuture = Future { loadEditionNames() }
      val racerMapFuture = async { extractRacerAliasMap(await(racerDaosFuture)) }
      val teamMap = await(teamsFuture).map { case t => t.name -> t }.toMap
      val editionsFuture = async { extractEditions(await(racerMapFuture), teamMap, await(editionNamesFuture)) }
      val racesFuture = async { extractRaces(await(raceNamesFuture), await(racerMapFuture)) }
      val racersFuture = async { await(racerDaosFuture).map { r => SingleRacer(r.id, r.name, r.flag) } }
      new ChicagoF1Data(await(racersFuture), await(teamsFuture), await(racesFuture), await(editionsFuture), await(videosFuture))
    }
    logger.info("Waiting...")
    val data = Await.result(asyncDataManager, 60 seconds)
    val stop = System.nanoTime()
    info(s"Loaded data manager in ${(stop - start) / oneMillion} millis")
    data
  }

  def extractEditions(racers: Map[String, String], teams: Map[String, Team], editions: Seq[String]): List[Edition] = {
    logger.info("Starting to extract editions")
    val editionResults: List[Edition] =
      editions.par.map(name =>
        ResultsImporter.readEdition(name, loadFileIntoString("edition/" + name + ".csv")))
        .map { e =>
          val racerResults = extractRacerResults(e, racers, teams)
          Edition(e.date, racerResults)
        }.toList
        .sortBy(_.date.toString)
        .reverse
    logger.info("Finished extracting editions")
    editionResults
  }

  private def extractRacerResults(e: Edition, racers: Map[String, String], teams: Map[String, Team]): Seq[RacerResult] = {
    e.results.filter { r =>
      racers.contains(r.racer.name) || teams.contains(r.racer.name)
    }.zipWithIndex
      .map { case (rr, index) =>
        RacerResult(RacerName(racers.getOrElse(rr.racer.name, rr.racer.name)), index + 1, rr.kart, rr.time, rr.penalty)
    }
  }

  def extractRaces(races: Seq[String], racerAliases: Map[String, String]): List[Race] = {
    logger.info("Starting to extract races")
    val racerResults =
      races.par
        .map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv")))
        .map(r => Race(r.date, r.time, r.results.map {
        rr =>
          val racer = racerAliases.get(rr.racer.name) match {
            case Some(racerName) => racerName
            case None => rr.racer.name
          }
          RacerResult(RacerName(racer), rr.position, rr.kart, rr.time)
      })).toList
        .sortBy(r => r.raceId).reverse.toList
    logger.info("Finished extracting races")
    racerResults
  }

  private def extractRacerAliasMap(racerDaos: Seq[SingleRacerDao]): Map[String, String] = {
    logger.info("Starting to load racer alias map")
    var racerMap = Map.empty[String, String]
    racerDaos.foreach { r =>
        val primaryName = r.name
        (primaryName :: r.aliases).foreach {
          n => racerMap = racerMap.updated(n, primaryName)
        }
    }
    logger.info("Finished loading racer alias map")
    racerMap
  }

  private def loadRaceNames(): Seq[String] = {
    logger.info("Starting to load race names")
    val races  = loadStringsFromFiles("races.txt", "extraRaces.txt")
    logger.info("Finished loading race names")
    races
  }

  private def loadEditionNames(): Seq[String] = {
    logger.info("Starting to load edition names")
    val editionNames = loadStringsFromFiles("editions.txt", "extraEditions.txt")
    logger.info("Finished loading edition names")
    editionNames
  }

  private def loadVideos(): List[Video] = {
    logger.info("Starting to load videos")
    try {
      val json = loadFileIntoString("videos.json")
      vd.deserializeVideos(json).toList
    } catch {
      case t: Throwable =>
        logger.error("Could not load videos", t)
        List.empty[Video]
    } finally {
      logger.info("Finished loading videos")
    }
  }

  private def loadTeams(): List[Team] = {
    logger.info("Starting to load videos")
    try {
      val json = loadFileIntoString("teams.json")
      td.deserializeTeams(json).toList
    } catch {
      case t: Throwable =>
        logger.error("Could not load videos", t)
        List.empty[Team]
    } finally {
      logger.info("Finished loading videos")
    }
  }

  private def loadRacers(): List[SingleRacerDao] = {
    logger.info("Starting to load racers")
    try {
      val json = loadFileIntoString("racers.json")
      rd.deserializeRacerDaos(json).toList
    } catch {
      case t: Throwable =>
        logger.error("Could not load racers", t)
        List.empty[SingleRacerDao]
    } finally {
      logger.info("Finished loading racers")
    }
  }
}
