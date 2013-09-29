package com.chicagof1.scraping

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.model.RacerResult
import org.joda.time.{LocalDate, Period}
import com.chicagof1.ResultsExporter
import org.joda.time.format.{DateTimeFormat}

class ScrapingSpec extends FunSpec with ShouldMatchers {
  val url = "http://www.testuri.com/"
  val resultsScraper = new RacerResultsScraper
  val raceScraper = new RaceScraper

  lazy val regularHtml = loadFileIntoString(testResourcesDir + "melrose.html")
  lazy val forwardedHtml = loadFileIntoString(testResourcesDir + "melrose-forwarded.html")
  lazy val doubleForwardedHtml = loadFileIntoString(testResourcesDir + "double-forwarded.html")
  lazy val yahooForwardedHtml = loadFileIntoString(testResourcesDir + "yahoo-forwarded.html")

  describe("A Race Results Scraper") {
    it("should extract results from a regular email") {
      val results: Seq[RacerResult] = resultsScraper.extract(regularHtml, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, 21, Period.seconds(32).plusMillis(889).toStandardDuration))
      results(11) should be(RacerResult("JakeTracy", 12, 6, Period.seconds(37).plusMillis(378).toStandardDuration))
    }

    it("should extract results from a forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(forwardedHtml, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, 21, Period.seconds(32).plusMillis(605).toStandardDuration))
      results(11) should be(RacerResult("SuperMario", 12, 13, Period.seconds(37).plusMillis(957).toStandardDuration))
      ResultsExporter.writeCsv("results.csv", results)
    }

    it("should extract results from a double forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(doubleForwardedHtml, url)
      results.size should be(8)
      results(0) should be(RacerResult("JCLARK18TEAMAR...", 1, 10, Period.seconds(29).plusMillis(466).toStandardDuration))
      results(7) should be(RacerResult("Number 1", 8, 17, Period.seconds(39).plusMillis(246).toStandardDuration))
      ResultsExporter.writeCsv("results.csv", results)
    }

    it("should extract results from a yahoo forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(yahooForwardedHtml, url)
      results.size should be(7)
      RacerResult("Justin", 1, 14, Period.seconds(30).plusMillis(373).toStandardDuration)
      results(0) should be(RacerResult("Justin", 1, 14, Period.seconds(30).plusMillis(373).toStandardDuration))
      results(6) should be(RacerResult("jDaWg",7,12,Period.seconds(38).plusMillis(241).toStandardDuration))
      ResultsExporter.writeCsv("results.csv", results)
    }
  }

  describe("A Race Scraper") {
    it("should extract a race from a regular email") {
      val race = raceScraper.extract(regularHtml, url)
      race.date.toString should be("2013-01-23")
      race.time.toString should be("09:18:00.000")
      ResultsExporter.writeCsv(race, "output")
    }

    it("should extract a race from a forwarded email") {
      val race = raceScraper.extract(forwardedHtml, url)
      race.date.toString should be("2013-01-23")
      race.time.toString should be("09:08:00.000")
      ResultsExporter.writeCsv(race, "output")
    }

    it("should extract a race from a double forwarded email") {
      val race = raceScraper.extract(doubleForwardedHtml, url)
      race.date.toString should be("2013-09-25")
      race.time.toString should be("09:15:00.000")
      ResultsExporter.writeCsv(race, "output")
    }
  }
}