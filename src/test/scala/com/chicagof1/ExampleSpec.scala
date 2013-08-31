package com.chicagof1

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers


class ExampleSpec extends FunSpec with ShouldMatchers {
  describe("My First Example Spec") {
    it("should add two numbers correctly") {
      val sum = 2 + 2
      sum should be(4)
    }
  }
}
