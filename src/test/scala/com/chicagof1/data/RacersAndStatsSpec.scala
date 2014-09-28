package com.chicagof1.data

import org.scalatest.{Matchers, FunSpec}
import com.chicagof1.model._
import com.chicagof1.parsing.{RacerSerializer, RacerDeserializer}
import org.joda.time.{LocalTime, LocalDate}
import com.github.nscala_time.time.Imports._
import com.chicagof1.model.Racer
import com.chicagof1.model.RacerDao
import com.chicagof1.model.RacerResult
import com.chicagof1.model.Race

class RacersAndStatsSpec extends FunSpec with Matchers {
  val rd = new RacerDeserializer
  val meesa = RacerDao(1, "Meesa Maeng", List("meesa"), "USA")
  val meesaJson = """{"id":1,"name":"Meesa Maeng","aliases":["meesa"],"flag":"USA"}"""

  val racerSeq = Seq(
    RacerDao(1, "Meesa Maeng", List("meesa"), "USA"),
    RacerDao(2, "Justin Royster", List("Justin"), "USA"),
    RacerDao(3, "David Quednau", List(), "USA")
  )
  val racersJson =
    """
      |{"racers":[
      |{"id":1,"name":"Meesa Maeng","aliases":["meesa"],"flag":"USA"},
      |{"id":2,"name":"Justin Royster","aliases":["Justin"],"flag":"USA"},
      |{"id":3,"name":"David Quednau","aliases":[],"flag":"USA"}]}""".stripMargin

  describe("Racer Parsing") {
    it("should parse a racer dao") {
      rd.deserializeRacerDao(meesaJson) should be(meesa)
    }

    it("should parse a seq of racer daos") {
      rd.deserializeRacerDaos(racersJson) should be(racerSeq)
    }
  }

  val rs = new RacerSerializer

  describe("Video Serialization") {
    it("should serialize a racer") {
      rs.serializeRacerDao(meesa) should be(meesaJson)
    }

    it("should parse a seq of videos") {
      rs.serializeRacerDaos(racerSeq) should be(racersJson.replaceAll("\\n", ""))
    }
  }

  describe("Racer with statistics") {
    val racers = List(Racer(1, "A", "USA"), Racer(2, "B", "USA"), Racer(3, "C", "USA"))
    val videos = List(
      Video("a", "B", "2014-02-01", "Melrose")
    )
    val dm = new InMemoryDataManager(Some(new ChicagoF1Data(
      racers,
      List(Race(LocalDate.parse("2014-01-01"), LocalTime.parse("20:00"),
        Seq(
          RacerResult("A", 1, 10, 29.seconds + 0.millis),
          RacerResult("B", 2, 2, 29.seconds + 500.millis),
          RacerResult("C", 3, 4, 30.seconds + 0.millis)
        ))),
      List(Edition(LocalDate.parse("2014-02-01"),
        Seq(
          RacerResult("A", 1, 10, 29.seconds + 200.millis),
          RacerResult("C", 2, 2, 29.seconds + 600.millis),
          RacerResult("B", 3, 4, 30.seconds + 0.millis)
      )),
        Edition(LocalDate.parse("2014-03-01"),
          Seq(
            RacerResult("A", 1, 10, 29.seconds + 200.millis),
            RacerResult("B", 2, 2, 29.seconds + 600.millis),
            RacerResult("C", 3, 4, 30.seconds + 0.millis)
          ))),
      videos
    )))

    it("should compute stats for a racer") {
      dm.racerStatsFor("A") should be(
        RacerWithStats(racers(0), 1, 40, 2, 1, 1.0, buildPositionCount(Seq(2)), 0)
      )
      dm.racerStatsFor("B") should be(
        RacerWithStats(racers(1), 2, 37, 0, 0, 2.5, buildPositionCount(Seq(0, 1, 1)), 1)
      )
      dm.racerStatsFor("C") should be(
        RacerWithStats(racers(2), 3, 37, 0, 0, 2.5, buildPositionCount(Seq(0, 1, 1)), 0)
      )
    }
  }

  private def buildPositionCount(counts: Seq[Int]): Seq[Int] = counts.padTo(24, 0)
}
