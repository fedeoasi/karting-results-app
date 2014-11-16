package com.chicagof1.scraping

import com.chicagof1.model.{RacerName, Race, RacerResult}
import com.chicagof1.Formatters.dateFormatter
import com.chicagof1.Formatters.timeFormatter
import com.chicagof1.scraping.ScraperHelper._
import org.jsoup.Jsoup
import org.joda.time.{LocalTime, LocalDate, Period}

class RacerResultsScraper extends Scraper[Seq[RacerResult]] {
  val defaultKartNumber = 0
  val kartIndexOldStyle = 2
  val nameIndexOldStyle = 3
  val timeIndexOldStyle = 4
  val nameIndex = 1
  val timeIndex = 2
  val second = 1
  val first = 0

  def extract(html: String, url: String): Seq[RacerResult] = {
    val doc = Jsoup.parse(html, url)
    extractItemsUsingSelector[RacerResult](doc, "table[width=95%] tr",
      el => el.children.get(0).text != "Pos") {
      el =>
        val ch = el.children
        val (name, time, kart) = if (ch.get(second).text.isEmpty) {
          //Old style html has an empty column
          val name = ch.get(nameIndexOldStyle).text
          val time = ch.get(timeIndexOldStyle).text
          val kart = ch.get(kartIndexOldStyle).text.toInt
          (name, time, kart)
        } else {
          val name = ch.get(nameIndex).text
          val time = ch.get(timeIndex).text
          (name, time, defaultKartNumber)
        }

        val position = ch.get(first).text.toInt
        val timeSplit = time.split("\\.")
        val lapTime = Period.seconds(timeSplit(0).toInt).plusMillis(timeSplit(1).toInt).toStandardDuration
        RacerResult(RacerName(name), position, kart, lapTime)
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
        (LocalDate.parse(split(0), dateFormatter), LocalTime.parse(split(1), timeFormatter))
      }
    }
    Race(dateAndTime(0)._1, dateAndTime(0)._2, racerResultsScraper.extract(html, url))
  }
}
