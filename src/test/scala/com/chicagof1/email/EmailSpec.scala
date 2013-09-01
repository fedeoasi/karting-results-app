package com.chicagof1.email

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.utils.SpecUtils._
import com.chicagof1.parsing.EmailParser

class EmailSpec extends FunSpec with ShouldMatchers {
  val parser: EmailParser = new EmailParser()

  describe("Email") {
    it("should be able to parse an email message") {
      val message = parser.parseEmail(testResourcesDir + "melrose-email.txt")

      message.getSubject should be("Melrose Park Indoor Grand Prix Heat Result")
      message.getHeader("Delivered-To")(0) should be("xxx@gmail.com")
      parser.getFirstBodyPartContentAsString(message) should include("html")
    }
  }
}
