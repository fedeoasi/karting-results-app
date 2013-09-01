package com.chicagof1.email

import javax.mail._
import com.chicagof1.parsing.EmailParser
import com.chicagof1.scraping.RaceScraper
import com.chicagof1.ResultsExporter

object ImapRaceExtractor {
  def main(args: Array[String]) {
    val emailParser: EmailParser = new EmailParser
    val raceScraper = new RaceScraper

    val props = System.getProperties()
    props.setProperty("mail.store.protocol", "imaps")
    val session = Session.getDefaultInstance(props, null)
    val store = session.getStore("imaps")
    try {
      // use imap.gmail.com for gmail
      val email: String = System.getenv("KRA_EMAIL")
      if(email == null || email.isEmpty) {
        throw new RuntimeException("You must set the email you want to use as the following environment variable: KRA_EMAIL." +
          " You will also need to set the password as KRA_PWD")
      }
      val pwd: String = System.getenv("KRA_PWD")
      store.connect("imap.gmail.com", email, pwd)
      val inbox = store.getFolder("Inbox")
      inbox.open(Folder.READ_ONLY)

      // limit this to 20 message during testing
      val messages = inbox.getMessages()

      val filteredMessages = messages.filter(
        m => {
          val subject = m.getSubject
          (subject.contains("Melrose") && subject.contains("Result")) ||
            subject.contains("Race Result")
        })
      println(s"There are ${filteredMessages.size} emails to be processed")
      for (message <- filteredMessages) {
        val content = emailParser.getFirstHtmlBodyPartContentAsString(message)
        val race = raceScraper.extract(content, "http://anything")
        ResultsExporter.writeCsv(race, "output")
      }
      inbox.close(true)
    } catch {
      case e: NoSuchProviderException => e.printStackTrace()
        System.exit(1)
      case me: MessagingException => me.printStackTrace()
        System.exit(2)
    } finally {
      store.close()
    }
  }
}