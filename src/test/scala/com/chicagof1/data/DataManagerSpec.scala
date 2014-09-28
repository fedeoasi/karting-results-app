package com.chicagof1.data

import org.scalatest.{Matchers, FunSpec}
import com.chicagof1.model._
import org.joda.time.{LocalTime, LocalDate}
import com.chicagof1.model.Edition
import com.chicagof1.model.RacerResult
import com.chicagof1.model.Video
import com.chicagof1.model.Race

class DataManagerSpec extends FunSpec with Matchers {
  val race11 = Race(LocalDate.parse("2013-01-01"), LocalTime.parse("08:00"), List[RacerResult]())
  val race12 = Race(LocalDate.parse("2013-01-01"), LocalTime.parse("09:00"), List[RacerResult]())
  val race21 = Race(LocalDate.parse("2013-02-01"), LocalTime.parse("08:00"), List[RacerResult]())

  val edition1 = Edition(LocalDate.parse("2013-01-01"), List[RacerResult]())
  val edition2 = Edition(LocalDate.parse("2013-02-01"), List[RacerResult]())

  val baseDataManager = InMemoryDataManager(Some(new ChicagoF1Data(
    List.empty[Racer],
    List[Race](
      race11,
      race12,
      race21
    ),
    List[Edition](
      edition1,
      edition2
    ),
    List[Video]()
  )))

  describe("DataManager") {
    it("should find an existent race") {
      val firstRace = baseDataManager.getRaceById("2013-01-01-08:00")
      firstRace should not be None
    }

    it("should return None for a a non-existent race") {
      val firstRace = baseDataManager.getRaceById("2013-01-01-08:30")
      firstRace should be(None)
    }

    it("should create an empty monthly championship when there is no data for the given period") {
      val champ = baseDataManager.buildMonthlyChampionship(
        "Chicago F1 2012",
        LocalDate.parse("2012-01-01"),
        LocalDate.parse("2012-12-01"))
      champ.editions.size should be(12)
      champ.editions.foreach(_.isInstanceOf[NonReportedEditionInChampionship] should be(true))
    }

    it("should create a monthly championship with data") {
      val champ = baseDataManager.buildMonthlyChampionship(
        "Chicago F1 2013",
        LocalDate.parse("2013-01-01"),
        LocalDate.parse("2013-12-01"))
      champ.editions.size should be(12)
      champ.editions(0) should be(ReportedEditionInChampionship(1, "Jan", edition1))
      champ.editions(1) should be(ReportedEditionInChampionship(2, "Feb", edition2))
      champ.editions(2) should be(NonReportedEditionInChampionship(3, "Mar"))
      champ.editions(11) should be(NonReportedEditionInChampionship(12, "Dec"))
    }

    it("should use the Chicago F1 points system") {
      val champ = baseDataManager.buildMonthlyChampionship(
        "Chicago F1 2013",
        LocalDate.parse("2013-01-01"),
        LocalDate.parse("2013-12-01"))
      champ.editions.size should be(12)
      val pointsFirstEdition = champ.pointsSystem.pointsForEdition(1)
      pointsFirstEdition.size should be(20)
      pointsFirstEdition(0) should be(40)
      pointsFirstEdition(19) should be(2)
      val pointsSecondEdition = champ.pointsSystem.pointsForEdition(2)
      pointsSecondEdition.size should be(20)
      pointsSecondEdition(0) should be(20)
      pointsSecondEdition(19) should be(1)
      val pointsLastEdition = champ.pointsSystem.pointsForEdition(12)
      pointsLastEdition.size should be(20)
      pointsLastEdition(0) should be(20)
      pointsLastEdition(19) should be(1)
      val pointsAfterLastEdition = champ.pointsSystem.pointsForEdition(13)
      pointsAfterLastEdition.size should be(0)
    }

    it("should build the race - edition relationship") {
      baseDataManager.editionsWithRaces should be(
        List(
          EditionWithRaces(edition1, List(race11, race12)),
          EditionWithRaces(edition2, List(race21))
        )
      )
    }
  }
}
