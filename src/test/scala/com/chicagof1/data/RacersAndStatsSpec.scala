package com.chicagof1.data

import org.scalatest.{Matchers, FunSpec}
import com.chicagof1.model.RacerDao
import com.chicagof1.parsing.{RacerSerializer, RacerDeserializer}

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
}
