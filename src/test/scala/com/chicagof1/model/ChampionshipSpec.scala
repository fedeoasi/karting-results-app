package com.chicagof1.model

import org.scalatest.Matchers
import org.scalatest.FunSpec
import org.joda.time.LocalDate
import com.github.nscala_time.time.Imports._

class ChampionshipSpec extends FunSpec with Matchers {
  val janRes = Seq[RacerResult](
    RacerResult(RacerName("A"), 1, 5, 1.minute + 1.second),
    RacerResult(RacerName("C"), 2, 8, 1.minute + 2.second),
    RacerResult(RacerName("B"), 3, 17, 1.minute + 5.second))

  val febRes = Seq[RacerResult](
    RacerResult(RacerName("C"), 1, 5, 1.minute + 1.second, Some(new HalfPointsPenalty())),
    RacerResult(RacerName("A"), 2, 8, 1.minute + 2.second),
    RacerResult(RacerName("D"), 3, 17, 1.minute + 3.second),
    RacerResult(RacerName("B"), 4, 18, 1.minute + 4.second))

  val marRes = Seq[RacerResult](
    RacerResult(Team("Team A", List("A", "B")), 1, 5, 1.minute + 1.second),
    RacerResult(Team("Team B", List("C", "D")), 2, 8, 1.minute + 2.second))

  val championship = Championship(
    "2014",
    Seq(
      ReportedEditionInChampionship(1, "Jan", Edition(LocalDate.parse("2014-01-05"), janRes)),
      ReportedEditionInChampionship(2, "Feb", Edition(LocalDate.parse("2014-02-11"), febRes)),
      ReportedEditionInChampionship(3, "Mar", Edition(LocalDate.parse("2014-02-11"), marRes)),
      NonReportedEditionInChampionship(4, "Apr")),
    new ChicagoF1PointsSystem(3),
    List()
  )

  describe("A Championship") {
    it("should create the correct standings") {
      val standings = championship.standings
      val racers = standings.orderedRacers
      racers.size should be(4)
      racers(0) should be(("A", 79))
      racers(1) should be(("B", 73))
      racers(2) should be(("C", 67))
      racers(3) should be(("D", 37))
    }
  }
}
