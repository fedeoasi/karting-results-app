package com.chicagof1.scraping

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.model.RacerResult
import org.joda.time.Period
import com.chicagof1.ResultsExporter
import com.github.nscala_time.time.Imports._

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
      results(0) should be(RacerResult("Justin", 1, 21, 32.seconds + 889.millis))
      results(11) should be(RacerResult("JakeTracy", 12, 6, 37.seconds + 378.millis))
    }

    it("should extract results from a forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(forwardedHtml, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, 21, 32.seconds + 605.millis))
      results(11) should be(RacerResult("SuperMario", 12, 13, 37.seconds + 957.millis))
      ResultsExporter.writeCsv("results.csv", results)
    }

    it("should extract results from a double forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(doubleForwardedHtml, url)
      results.size should be(8)
      results(0) should be(RacerResult("JCLARK18TEAMAR...", 1, 10, 29.seconds + 466.millis))
      results(7) should be(RacerResult("Number 1", 8, 17, 39.seconds + 246.millis))
      ResultsExporter.writeCsv("results.csv", results)
    }

    it("should extract results from a yahoo forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(yahooForwardedHtml, url)
      results.size should be(7)
      RacerResult("Justin", 1, 14, 30.seconds + 373.millis)
      results(0) should be(RacerResult("Justin", 1, 14, 30.seconds + 373.millis))
      results(6) should be(RacerResult("jDaWg", 7, 12, 38.seconds + 241.millis))
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