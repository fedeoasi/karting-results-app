package com.chicagof1.utils

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import DateUtils._

class DateUtilsSpec extends FunSpec with ShouldMatchers {
  describe("DateUtils") {
    it("should create a month") {
      val jan = month("2014-01-01")
      jan.contains(dateTime(2013, 12, 31)) should be(false)
      jan.contains(dateTime(2014,  1,  1)) should be(true)
      jan.contains(dateTime(2014,  1, 16)) should be(true)
      jan.contains(dateTime(2014,  1, 31)) should be(true)
      jan.contains(dateTime(2014,  2,  1)) should be(false)
    }

    it("should enumerate the months between two dates") {
      val months2014 = monthsBetween(dateTime(2014,  1,  1), dateTime(2014,  12,  1))
      months2014.size should be(12)
    }
  }
}
