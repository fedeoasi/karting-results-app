package com.chicagof1

import org.joda.time.format.DateTimeFormat

object Formatters {
  val dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy")
  val timeFormatter = DateTimeFormat.forPattern("hh:mm")
  val datePrintFormatter = DateTimeFormat.forPattern("MM-dd-yyyy")
}
