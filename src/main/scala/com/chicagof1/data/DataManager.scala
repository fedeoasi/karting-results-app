package com.chicagof1.data

import com.chicagof1.model.Race
import com.chicagof1.ResultsImporter
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging


object DataProvider extends Logging {
  def dataManager(): DataManager = {
    val races = loadFileIntoString("races.txt").split("\\n")
    val racerResults: List[Race] =
      races.map(name => ResultsImporter.readRace(name, loadFileIntoString(name + ".csv"))).toList
    new DataManager(racerResults)
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

case class DataManager(races: List[Race]) {
  private val racesMap: Map[String, Race] = races.map(r => r.raceId -> r) toMap

  def getRaceById(id: String): Option[Race] = racesMap.get(id)
}