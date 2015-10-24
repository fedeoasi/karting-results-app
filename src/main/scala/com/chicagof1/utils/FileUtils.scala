package com.chicagof1.utils

import java.io.StringWriter
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging

object FileUtils extends Logging {
  def loadFileIntoString(path: String): String = {
    loadFileIntoStringOption(path).getOrElse("")
  }

  def loadFileIntoStringOption(path: String): Option[String] = {
    debug("Opening resource at path: " + path)
    val classLoader = Thread.currentThread().getContextClassLoader
    val optionalStream = Option(classLoader.getResourceAsStream(path))
    if(optionalStream.isEmpty) {
      error("Unable to stream resource at path: " + path)
    }
    optionalStream.flatMap { racesStream =>
      val writer = new StringWriter()
      try {
        IOUtils.copy(racesStream, writer, "UTF-8")
        Some(writer.toString)
      } catch {
        case t: Throwable =>
          error("Error while processing resource: " + path.toString, t)
          None
      }
    }
  }

  def loadStringsFromFiles(filenames: String*): Seq[String] = {
    filenames.flatMap { f =>
      loadFileIntoString(f).split("\\n").filterNot(_.isEmpty)
    }
  }
}
