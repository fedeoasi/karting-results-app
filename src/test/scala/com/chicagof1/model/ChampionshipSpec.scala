package com.chicagof1.model

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import org.joda.time.LocalDate
import com.github.nscala_time.time.Imports._

class ChampionshipSpec extends FunSpec with ShouldMatchers {
  val janRes = Seq[RacerResult](
    RacerResult("A", 1, 5, 1.minute + 1.second),
    RacerResult("C", 2, 8, 1.minute + 2.second),
    RacerResult("B", 3, 17, 1.minute + 5.second))

  val febRes = Seq[RacerResult](
    RacerResult("C", 1, 5, 1.minute + 1.second),
    RacerResult("A", 2, 8, 1.minute + 2.second),
    RacerResult("D", 3, 17, 1.minute + 3.second),
    RacerResult("B", 4, 18, 1.minute + 4.second))

  val championship = Championship(
    "2014",
    Seq(
      ReportedEditionInChampionship(1, "Jan", Edition(LocalDate.parse("2014-01-05"), janRes)),
      ReportedEditionInChampionship(2, "Feb", Edition(LocalDate.parse("2014-02-11"), febRes)),
      NonReportedEditionInChampionship(3, "Mar")),
    new ChicagoF1PointsSystem(3)
  )

  describe("A Championship") {
    it("should create the correct standings") {
      val standings = championship.standings
      val racers = standings.orderedRacers
      racers.size should be(4)
      racers(0) should be(("A", 59))
      racers(3) should be(("D", 18))
    }


  }
}
