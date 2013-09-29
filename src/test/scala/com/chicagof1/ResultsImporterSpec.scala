package com.chicagof1

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.model.RacerResult
import org.joda.time.Period

class ResultsImporterSpec extends FunSpec with ShouldMatchers {
  val racerResultCsv = testResourcesDir + "2013-01-23 - 08:48.csv"
  val raceCsv = testResourcesDir + "2012-11-16.csv"

  describe("A Results Importer") {
    it("should read a racer result from a csv") {
      val csv = loadFileIntoString(racerResultCsv)
      val result = ResultsImporter.readRacerResult(csv)
      result.size should be(11)
      result(0) should be(RacerResult("Justin", 1, 16, Period.seconds(33).plusMillis(720).toStandardDuration))
      result(10) should be(RacerResult("SuperMario", 11, 8, Period.seconds(39).plusMillis(690).toStandardDuration))
    }
  }
}
