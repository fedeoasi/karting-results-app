package com.chicagof1

import org.joda.time.format.DateTimeFormat

object Formatters {
  val dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy")
  val prettyDateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy")
  val timeFormatter = DateTimeFormat.forPattern("hh:mm")
  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm")
  val datePrintFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
}
