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

  describe("A Race Results Scraper") {
    it("should extract results from a regular email") {
      val results: Seq[RacerResult] = resultsScraper.extract(regularHtml, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, Period.seconds(32).plusMillis(889).toStandardDuration))
      results(11) should be(RacerResult("JakeTracy", 12, Period.seconds(37).plusMillis(378).toStandardDuration))
    }

    it("should extract results from a forwarded email") {
      val results: Seq[RacerResult] = resultsScraper.extract(forwardedHtml, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, Period.seconds(32).plusMillis(605).toStandardDuration))
      results(11) should be(RacerResult("SuperMario", 12, Period.seconds(37).plusMillis(957).toStandardDuration))
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
  }
}