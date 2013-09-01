package com.chicagof1.utils

import scala.io.Source
import java.io.{StringReader, FileOutputStream}
import org.apache.commons.io.IOUtils

object SpecUtils {
  val testResourcesDir = "src/test/resources/"

  def loadFileIntoString(filename: String): String = {
    Source.fromFile(filename)
      .getLines()
      .mkString("\n")
  }

  def writeStringIntoFile(string: String, path: String) {
    val outputStream: FileOutputStream = new FileOutputStream(path)
    IOUtils.copy(new StringReader(string), outputStream)
    outputStream.close
  }
}

