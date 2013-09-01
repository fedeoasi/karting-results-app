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
    writer.writeAll(results.map(r => Seq(r.name, r.position, r.time.getStandardSeconds + ":" + r.time.getMillis % 1000)))
  }

  def writeCsv(race: Race, baseFolder: String) {
    val fileName = datePrintFormatter.print(race.date) + " - " + timeFormatter.print(race.time)
    val f = new File(baseFolder + File.separator + fileName + ".csv")
    val writer = CSVWriter.open(f)
    writer.writeRow(Seq("Racer", "Position", "Kart #", "Time"))
    writer.writeAll(race.results.map(r => Seq(r.name, r.position, r.kart, r.time.getStandardSeconds + ":" + r.time.getMillis % 1000)))
  }

  def writeCsv(race: Edition, baseFolder: String) {
    val fileName = datePrintFormatter.print(race.date)
    val f = new File(baseFolder + File.separator + fileName + ".csv")
    val writer = CSVWriter.open(f)
    writer.writeRow(Seq("Racer", "Kart #", "Time"))
    writer.writeAll(race.results.map(r => Seq(r.name, r.kart, r.time.getStandardSeconds + ":" + r.time.getMillis % 1000)))
  }
}
