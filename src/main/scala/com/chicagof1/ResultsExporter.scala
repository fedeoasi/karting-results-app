package com.chicagof1

import com.chicagof1.model.RacerResult
import java.io.File
import com.github.tototoshi.csv.CSVWriter

object ResultsExporter {
  def writeCsv(fileLocation: String, results: Seq[RacerResult]) {
    val f = new File(fileLocation)
    val writer = CSVWriter.open(f)
    writer.writeAll(results.map(r => Seq(r.name, r.position, r.time.getStandardSeconds + ":" + r.time.getMillis % 1000)))
  }
}
