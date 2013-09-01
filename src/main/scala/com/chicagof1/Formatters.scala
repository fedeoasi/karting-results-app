package com.chicagof1

import org.joda.time.format.DateTimeFormat

object Formatters {
  val dateFormatter = DateTimeFormat.forPattern("M/D/yyyy")
  val timeFormatter = DateTimeFormat.forPattern("h:m")
}
