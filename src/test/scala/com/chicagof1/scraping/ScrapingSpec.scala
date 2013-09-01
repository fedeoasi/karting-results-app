package com.chicagof1.scraping

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.model.RacerResult
import org.joda.time.{Period, Duration}
import java.io.File
import com.github.tototoshi.csv.CSVWriter

class ScrapingSpec extends FunSpec with ShouldMatchers {
  val url = "http://www.testuri.com/"
  val scraper = new RacerResultsScraper

  describe("Scraping Karting Pages") {
    it("should extract results from a forwarded email") {
      lazy val html = loadFileIntoString(testResourcesDir + "melrose-forwarded.html")
      val results: Seq[RacerResult] = scraper.extract(html, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, Period.seconds(32).plusMillis(605).toStandardDuration))
      results(11) should be(RacerResult("SuperMario", 12, Period.seconds(37).plusMillis(957).toStandardDuration))
      val f = new File("results.csv")
      val writer = CSVWriter.open(f)
      writer.writeAll(results.map(r => Seq(r.name, r.position, r.time.getStandardSeconds + ":" + r.time.getMillis % 1000)))
    }

    it("should extract results from a regular email") {
      lazy val html = loadFileIntoString(testResourcesDir + "melrose.html")
      val results: Seq[RacerResult] = scraper.extract(html, url)
      results.size should be(12)
      results(0) should be(RacerResult("Justin", 1, Period.seconds(32).plusMillis(889).toStandardDuration))
      results(11) should be(RacerResult("JakeTracy", 12, Period.seconds(37).plusMillis(378).toStandardDuration))
    }
  }
}
