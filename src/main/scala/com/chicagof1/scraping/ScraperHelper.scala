package com.chicagof1.scraping

import org.jsoup.nodes.{Element, Document}
import scala.collection.JavaConversions._
import grizzled.slf4j.Logging
import scala.reflect.runtime.universe._

object ScraperHelper extends Logging {
  private[this] val defaultValues = Map(
    typeTag[String] -> "")

  def extractItemUsingSelector[T: TypeTag](
    document: Document,
    selector: String,
    defaultValue: Option[T] = None)
    (transform: Element => T): T = {

    val result = document.select(selector)
    result.headOption match {
      case Some(element) => {
        try {
          transform(element)
        } catch {
          case e: Throwable => {
            logger.error(s"Error while processing url: ${document.baseUri}")
            getDefault(defaultValue)
          }
        }
      }
      case None => getDefault(defaultValue)
    }
  }


  def extractItemsUsingSelector[T](
    document: Document,
    selector: String,
    include: (Element => Boolean) = _ => true)
    (transform: Element => T): Seq[T] = {

    def extracted: Element => Option[T] =
      element =>
        try {
          if (include(element)) Some(transform(element)) else None
        } catch {
          case e: Throwable => {
            logger.error(s"Error while processing url: ${document.baseUri}")
            None
          }
        }
    document.select(selector).flatMap(extracted(_))
  }

  private[this] def getDefault[T: TypeTag](defaultValue: Option[T]): T = {
    defaultValue match {
      case Some(d) => d
      case None => {
        try {
          defaultValues.get(typeTag[T]).asInstanceOf[T]
        } catch {
          case e: Throwable =>
            throw new RuntimeException("No default value found for type: " + typeTag[T])
        }
      }
    }
  }
}
