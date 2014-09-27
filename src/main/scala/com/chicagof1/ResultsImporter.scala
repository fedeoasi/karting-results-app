package com.chicagof1

import com.chicagof1.model._
import com.github.tototoshi.csv.CSVReader
import java.io.StringReader
import org.joda.time.{Duration, LocalTime, LocalDate, Period}
import scala.util.matching.Regex
import com.chicagof1.model.RacerResult
import com.chicagof1.model.Race
import com.chicagof1.model.Edition
import scala.Some

object ResultsImporter {
  val minSecRegex = new Regex("(\\d+):(\\d+)", "secs", "millis")
  lazy val halfPointsPenalty = new HalfPointsPenalty

  def readRacerResult(contents: String): List[RacerResult] = {
    val reader = CSVReader.open(new StringReader(contents))
    reader.allWithHeaders().zipWithIndex.map {
      case (rowMap, rowNum) =>
        val time = extractTime(rowMap)
        val position = rowMap.get("Position") match {
          case Some(pos) => pos.toInt
          case None => rowNum + 1
        }
        val racer = rowMap.get("Racer").get
        val penalty = extractPenalty(rowMap, racer)
        val kart = rowMap.get("Kart #").get.toInt
        RacerResult(racer, position, kart, time, penalty)
    }
  }

  def readRace(filename: String, contents: String): Race = {
    val split = filename.replaceAll(".csv", "").split(" - ")
    val date = new LocalDate(split(0))
    val time = new LocalTime(split(1))
    val results = readRacerResult(contents)
    Race(date, time, results)
  }

  def readEdition(filename: String, contents: String): Edition = {
    val date = new LocalDate(filename.replaceAll(".csv", ""))
    val results = readRacerResult(contents)
    Edition(date, results)
  }

  private def extractPenalty(rowMap: Map[String, String], racer: String): Option[HalfPointsPenalty] = {
    rowMap.get("Penalty").flatMap { p =>
      if (p == "true") {
        Some(halfPointsPenalty)
      } else None
    }
  }

  private def extractTime(rowMap: Map[String, String]): Duration = {
    rowMap.get("Time") match {
      case Some(time) =>
        val timeSplit = rowMap.get("Time").get.split(":")
        Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration
      case None =>
        rowMap.get("Gap") match {
          case Some(gap) => parseGap(gap)
          case None => throw new IllegalStateException("Both Time and Gap are not defined")
        }
    }
  }

  private def parseGap(gap: String): Duration = {
    minSecRegex.findFirstMatchIn(gap) match {
      case Some(r) =>
        Period
          .seconds(r.group("secs").toInt)
          .plusMillis(r.group("millis").toInt)
          .toStandardDuration
      case None => Period.millis(0).toStandardDuration
    }
  }
}
