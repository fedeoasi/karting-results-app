package com.chicagof1

import com.chicagof1.model.{Edition, Race, RacerResult}
import com.chicagof1.Formatters._
import java.io.File
import com.github.tototoshi.csv.CSVWriter

object ResultsExporter {
  def writeCsv(fileLocation: String, results: Seq[RacerResult]) {
    val f = new File(fileLocation  + ".csv")
    val writer = CSVWriter.open(f)
    writer.writeRow(Seq("Racer", "Position", "Time"))
    writer.writeAll(results.map(r => Seq(r.racer.name, r.position, r.time.getStandardSeconds +
      ":" + "%03d".format(r.time.getMillis % 1000))))
  }

  def writeCsv(race: Race, baseFolder: String) {
    val fileName = raceFilename(race)
    val f = new File(baseFolder + File.separator + fileName + ".csv")
    val writer = CSVWriter.open(f)
    writer.writeRow(Seq("Racer", "Position", "Kart #", "Time"))
    writer.writeAll(race.results.map(r => Seq(r.racer.name, r.position, r.kart,
      r.formattedTime)))
  }

  def writeCsv(race: Edition, baseFolder: String) {
    val fileName = editionFilename(race)
    val f = new File(baseFolder + File.separator + fileName + ".csv")
    val writer = CSVWriter.open(f)
    writer.writeRow(Seq("Racer", "Kart #", "Time"))
    writer.writeAll(race.results.map(r => Seq(r.racer.name, r.kart,
      r.time.getStandardSeconds + ":%03d".format(r.time.getMillis % 1000))))
  }

  def editionFilename(race: Edition): String = {
    datePrintFormatter.print(race.date)
  }

  def raceFilename(race: Race): String = {
    datePrintFormatter.print(race.date) + " - " + timeFormatter.print(race.time)
  }
}
