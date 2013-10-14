package com.chicagof1.data

import com.chicagof1.model.{RacerResult, Edition, Race}
import com.chicagof1.ResultsImporter
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging


object DataProvider extends Logging {
  def dataManager(): DataManager = {
    val racers: Seq[String] = loadFileIntoString("racers.txt").split("\\n")
    val races = loadFileIntoString("races.txt").split("\\n")
    val racerResults: List[Race] =
      races.map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv")))
        .sortBy(r => r.raceId).reverse.toList
    val editions = loadFileIntoString("editions.txt").split("\\n")
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
    new DataManager(racerResults, editionResults)
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

case class DataManager(races: List[Race], editions: List[Edition]) {
  private val racesMap: Map[String, Race] = races.map(r => r.raceId -> r) toMap
  private val editionsMap: Map[String, Edition] = editions.map(e => e.date.toString -> e) toMap

  def getRaceById(id: String): Option[Race] = racesMap.get(id)
  def getEditionById(id: String): Option[Edition] = editionsMap.get(id)
}