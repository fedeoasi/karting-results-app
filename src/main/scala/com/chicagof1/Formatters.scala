package com.chicagof1

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeZone

object Formatters {
  val dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy")
  val prettyDateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy")
  val timeFormatter = DateTimeFormat.forPattern("hh:mm")
  val cstTimeFormatter = DateTimeFormat.forPattern("hh:mm a").withZone(DateTimeZone.forID("America/Chicago"))
  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm")
  val datePrintFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
}
