package com.chicagof1.data

import com.chicagof1.model.RacerResult
import com.chicagof1.ResultsImporter
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging


object DataProvider extends Logging {
  def dataManager(): DataManager = {
    val races = loadFileIntoString("races.txt").split("\n")
    val racerResults: Map[String, List[RacerResult]] =
      races.map { r => r -> ResultsImporter.readRacerResult(r) } toMap;
    new DataManager(racerResults)
  }

  def loadFileIntoString(path: String): String = {
    val racesStream = Thread.currentThread().getContextClassLoader.getResourceAsStream("races.txt")
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

case class DataManager(races: Map[String, List[RacerResult]])