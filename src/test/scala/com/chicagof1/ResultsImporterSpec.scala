package com.chicagof1

import org.scalatest.{Matchers, FunSpec}
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.model.RacerResult
import org.joda.time.Period

class ResultsImporterSpec extends FunSpec with Matchers {
  val raceFilename: String = "2013-01-23 - 08:48.csv"
  val raceGapsFilename: String = "2014-01-01 - 01:01.csv"
  val racerResultCsv = testResourcesDir + raceFilename
  val racerResultGapsCsv = testResourcesDir + raceGapsFilename
  val editionFilename: String = "2012-11-16.csv"
  val raceCsv = testResourcesDir + editionFilename

  describe("A Results Importer") {
    it("should read a racer result from csv contents") {
      val csv = loadFileIntoString(racerResultCsv)
      val result = ResultsImporter.readRacerResult(csv)
      result.size should be(11)
      result(0) should be(RacerResult("Justin", 1, 16, Period.seconds(33).plusMillis(720).toStandardDuration))
      result(10) should be(RacerResult("SuperMario", 11, 8, Period.seconds(39).plusMillis(690).toStandardDuration))
    }

    it("should read a racer result with gaps") {
      val csv = loadFileIntoString(racerResultGapsCsv)
      val result = ResultsImporter.readRacerResult(csv)
      result.size should be(24)
      result(0) should be(RacerResult("Federico Caimi", 1, 22, Period.seconds(0).toStandardDuration))
      result(1) should be(RacerResult("Ken Lim", 2, 30, Period.seconds(4).plusMillis(893).toStandardDuration))
      result(23) should be(RacerResult("James Moeller", 24, 10, Period.seconds(0).toStandardDuration))
    }

    it("should read a race from a csv file") {
      val csv = loadFileIntoString(racerResultCsv)
      val race = ResultsImporter.readRace(raceFilename, csv)
      race.date.toString should be("2013-01-23")
      race.time.toString should be("08:48:00.000")
      race.results.size should be(11)
      race.results(0) should be(RacerResult("Justin", 1, 16, Period.seconds(33).plusMillis(720).toStandardDuration))
      race.results(10) should be(RacerResult("SuperMario", 11, 8, Period.seconds(39).plusMillis(690).toStandardDuration))
    }

    it("should read an edition from a csv file") {
      val csv = loadFileIntoString(racerResultCsv)
      val race = ResultsImporter.readEdition(editionFilename, csv)
      race.date.toString should be("2012-11-16")
      race.results.size should be(11)
      race.results(0) should be(RacerResult("Justin", 1, 16, Period.seconds(33).plusMillis(720).toStandardDuration))
      race.results(10) should be(RacerResult("SuperMario", 11, 8, Period.seconds(39).plusMillis(690).toStandardDuration))
    }
  }
}
