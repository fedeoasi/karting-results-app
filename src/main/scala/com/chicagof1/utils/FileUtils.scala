package com.chicagof1.utils

import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging

object FileUtils extends Logging {
  def loadFileIntoString(path: String): String = {
    debug("Opening resource at path: " + path)
    val racesStream = Thread.currentThread().getContextClassLoader.getResourceAsStream(path)
    if(racesStream == null) {
      error("Unable to stream resource at path: " + path)
    }
    val writer = new StringWriter()
    try {
      IOUtils.copy(racesStream, writer, "UTF-8")
      writer.toString
    } catch {
      case t: Throwable => {
        error("Error while processing resource: " + path.toString)
        println(t.printStackTrace())
      }
        ""
    }
  }

  def loadStringsFromFiles(filenames: String*): Seq[String] = {
    filenames.flatMap {
      case f => loadFileIntoString(f).split("\\n").filterNot(_.isEmpty)
    }
  }
}
