package com.chicagof1.email

import javax.mail._
import com.chicagof1.parsing.EmailParser
import com.chicagof1.scraping.RaceScraper
import com.chicagof1.ResultsExporter
import com.chicagof1.model.{Race, RacerResult, Edition}
import org.joda.time.Duration
import java.io.FileOutputStream
import grizzled.slf4j.Logging

object Joda {
  implicit def dateTimeOrdering: Ordering[Duration] = Ordering.fromLessThan(_ isShorterThan  _)
}

import Joda._

// scalastyle:off null
object ImapRaceExtractor extends Logging {
  val emailParser: EmailParser = new EmailParser
  val raceScraper = new RaceScraper

  def main(args: Array[String]) {
    val props = System.getProperties
    props.setProperty("mail.store.protocol", "imaps")
    val session = Session.getDefaultInstance(props, null)
    val store = session.getStore("imaps")
    try {
      val (email, pwd) = retrieveCredentials()
      store.connect("imap.gmail.com", email, pwd)
      val inbox = store.getFolder("Inbox")
      inbox.open(Folder.READ_ONLY)

      val messages = inbox.getMessages
      val filteredMessages = filterMessages(messages)
      info(s"There are ${filteredMessages.size} emails to process")

      val races = filteredMessages.map { message =>
          val content = emailParser.getFirstHtmlBodyPartContentAsString(message)
          raceScraper.extract(content, "http://anything")
      }

      val racesString = races.map(ResultsExporter.raceFilename).mkString("\n")
      new FileOutputStream("output/races.txt").write(racesString.getBytes)

      races.foreach(ResultsExporter.writeCsv(_, "output"))

      val editions = extractEditions(races)
      editions.foreach { e => ResultsExporter.writeCsv(e, "output/edition") }
      val editionsString = editions.map(ResultsExporter.editionFilename).mkString("\n")
      new FileOutputStream("output/editions.txt").write(editionsString.getBytes)
      inbox.close(true)
    } catch {
      case e: NoSuchProviderException => e.printStackTrace()
      case me: MessagingException => me.printStackTrace()
    } finally {
      store.close()
    }
  }

  def filterMessages(messages: Array[Message]): Array[Message] = {
    messages.filter { m =>
      val subject = m.getSubject.toLowerCase
      (subject.contains("melrose") && subject.contains("result")) ||
        subject.contains("race")
    }
  }

  def retrieveCredentials(): (String, String) = {
    val email: String = System.getenv("KRA_EMAIL")
    if (email == null || email.isEmpty) {
      throw new RuntimeException("You must set the email you want to use as the following environment variable: KRA_EMAIL." +
        " You will also need to set the password as KRA_PWD")
    }
    val pwd: String = System.getenv("KRA_PWD")
    (email, pwd)
  }

  def extractEditions(races: Array[Race]): Seq[Edition] = {
    races.groupBy(_.date).map {
      case (date, _) =>
        val bestResultByName: Seq[RacerResult] = races
          .flatMap(_.results)
          .groupBy(_.racer.name)
          .map {
          case (name, results) => results.minBy(_.time)
        }.toSeq
        (date, bestResultByName)
    }.map {
      case (date, results) => Edition(date, results.sortBy(_.time))
    }.toSeq
  }
}
// scalastyle:on null
