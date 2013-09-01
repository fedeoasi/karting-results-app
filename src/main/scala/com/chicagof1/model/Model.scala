package com.chicagof1.model

import org.joda.time.{LocalTime, LocalDate, Duration}

case class RacerResult(name: String, position: Int, kart: Int, time: Duration)
case class EditionResult(name: String, kart: Int, time: Duration)

case class Race(date: LocalDate, time: LocalTime, results: Seq[RacerResult])

case class Edition(date: LocalDate, results: Seq[RacerResult])