package com.chicagof1

import com.chicagof1.model.{Edition, Race, RacerResult}
import com.github.tototoshi.csv.CSVReader
import java.io.StringReader
import org.joda.time.{LocalTime, LocalDate, Period}

object ResultsImporter {
  def readRacerResult(contents: String): List[RacerResult] = {
    val reader = CSVReader.open(new StringReader(contents))
    reader.allWithHeaders().map(r => {
      val timeSplit = r.get("Time").get.split(":")
      RacerResult(
        r.get("Racer").get,
        r.get("Position").get.toInt,
        r.get("Kart #").get.toInt,
        Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration)
    })
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
}
