package com.chicagof1.scraping

import com.chicagof1.model.{Race, RacerResult}
import com.chicagof1.Formatters.dateFormatter
import com.chicagof1.Formatters.timeFormatter
import com.chicagof1.scraping.ScraperHelper._
import org.jsoup.Jsoup
import org.joda.time.{LocalTime, LocalDate, Period}

class RacerResultsScraper extends Scraper[Seq[RacerResult]] {
  def extract(html: String, url: String): Seq[RacerResult] = {
    val doc = Jsoup.parse(html, url)
    extractItemsUsingSelector[RacerResult](doc, "table[width=95%] tr",
      el => el.children.get(0).text != "Pos") {
      el => {
        val ch = el.children

        var name = ""
        var time = ""
        var kart = 0
        if(ch.get(1).text.isEmpty) {
          //Old style html has an empty column
          name = ch.get(3).text
          time = ch.get(4).text
          kart = ch.get(2).text.toInt
        } else {
          name = ch.get(1).text
          time = ch.get(2).text
        }

        val position = ch.get(0).text.toInt
        val timeSplit = time.split("\\.")
        val lapTime = Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration
        RacerResult(name, position, kart, lapTime)
      }
    }
  }
}

class RaceScraper extends Scraper[Race] {
  val racerResultsScraper = new RacerResultsScraper

  def extract(html: String, url: String): Race = {
    val doc = Jsoup.parse(html, url)
    val dateAndTime: Seq[(LocalDate, LocalTime)] =
      extractItemsUsingSelector[(LocalDate, LocalTime)](doc, "table tr td[width=50%]",
      el => el.text.contains("Laptimes")) {
      el => {
        val processed = el.text.replaceAll("\\(.*\\)", "").replaceAll("Laptimes", "").replace("\u00a0","").trim
        val split = processed.split("\\s")
        println(processed)
        ((LocalDate.parse(split(0), dateFormatter), LocalTime.parse(split(1), timeFormatter)))
      }
    }
    Race(dateAndTime(0)._1, dateAndTime(0)._2, racerResultsScraper.extract(html, url))
  }
}
