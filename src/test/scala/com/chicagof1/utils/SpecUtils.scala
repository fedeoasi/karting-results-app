package com.chicagof1.utils

import scala.io.Source

object SpecUtils {
  val testResourcesDir = "src/test/resources/"

  def loadFileIntoString(filename: String): String = {
    Source.fromFile(filename)
      .getLines()
      .mkString("\n")
  }
}

