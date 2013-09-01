package com.chicagof1.scraping

import com.chicagof1.model.RacerResult
import com.chicagof1.scraping.ScraperHelper._
import org.jsoup.Jsoup
import org.joda.time.Period

class RacerResultsScraper extends Scraper[Seq[RacerResult]] {
  def extract(html: String, url: String): Seq[RacerResult] = {
    val doc = Jsoup.parse(html, url)
    extractItemsUsingSelector[RacerResult](doc, "table[rules=all] tr",
      el => el.children.get(0).text != "Pos") {
      el => {
        val ch = el.children
        val timeSplit = ch.get(4).text.split("\\.")
        val time = Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration
        RacerResult(ch.get(3).text, ch.get(0).text.toInt, time)
      }
    }
  }
}
