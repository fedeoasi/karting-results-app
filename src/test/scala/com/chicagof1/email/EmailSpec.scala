package com.chicagof1.email

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.parsing.EmailParser
import org.jsoup.Jsoup
import com.chicagof1.utils.SpecUtils

class EmailSpec extends FunSpec with ShouldMatchers {
  val parser: EmailParser = new EmailParser()

  describe("Email") {
    it("should parse an email message") {
      val message = parser.parseEmail(testResourcesDir + "melrose-email.txt")

      message.getSubject should be("Melrose Park Indoor Grand Prix Heat Result")
      message.getHeader("Delivered-To")(0) should be("xxx@gmail.com")
      parser.getFirstBodyPartContentAsString(message) should include("html")
    }

    it("should extract html from the basic karting results email") {
      val message = parser.parseEmail(testResourcesDir + "melrose-email.txt")

      val html: String = parser.getFirstBodyPartContentAsString(message)
      html should include("html")
      Jsoup.parse(html)
      SpecUtils.writeStringIntoFile(html, "melrose.html")
    }

    it("should extract html from the forwarded karting results email") {
      val message = parser.parseEmail(testResourcesDir + "melrose-forwarded-email.txt")

      val html: String = parser.getFirstHtmlBodyPartContentAsString(message)
      SpecUtils.writeStringIntoFile(html, "melrose-forwarded.html")
      html should include("html")
      Jsoup.parse(html)
    }
  }
}
