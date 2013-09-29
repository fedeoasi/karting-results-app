package com.chicagof1

import com.chicagof1.model.RacerResult
import com.github.tototoshi.csv.CSVReader
import java.io.StringReader
import org.joda.time.Period

object ResultsImporter {
  def readRacerResult(csvString: String): List[RacerResult] = {
    val reader = CSVReader.open(new StringReader(csvString))
    reader.allWithHeaders().map(r => {
      val timeSplit = r.get("Time").get.split(":")
      RacerResult(
        r.get("Racer").get,
        r.get("Position").get.toInt,
        r.get("Kart #").get.toInt,
        Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration)
    })
  }
}
