package com.chicagof1.model

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.JValue
import scala.language.implicitConversions

case class Championship(name: String, editions: Seq[EditionInChampionship], pointsSystem: PointsSystem, teams: List[Team]) {
  lazy val standings: Standings = new Standings(editions, pointsSystem, teams)
}

trait EditionInChampionship {
  def number: Int
  def name: String
}

case class ReportedEditionInChampionship(number: Int, name: String, edition: Edition) extends EditionInChampionship
case class NonReportedEditionInChampionship(number: Int, name: String) extends EditionInChampionship

trait PointsSystem {
  def pointsForEdition(number: Int): Seq[Int]
}

case class StandingResult(position: Int, points: Int, kart: Int)

class ChicagoF1PointsSystem(n: Int) extends PointsSystem {
  val basePoints = 20.to(1, -1).toSeq

  def pointsForEdition(number: Int): Seq[Int] = {
    if(number == 1 || number == 11) {
      basePoints.map(_ * 2)
    } else if(number > 1 && number <= n) {
      basePoints
    } else {
      Seq.empty[Int]
    }
  }
}

class EmptyPointsSystem extends PointsSystem {
  def pointsForEdition(number: Int): Seq[Int] = Seq.empty[Int]
}

class Standings(editions: Seq[EditionInChampionship], pointsSystem: PointsSystem, teams: List[Team]) {
  val standingsByRace: Seq[Map[String, Standing]] = {
    editions.map {
      case r: ReportedEditionInChampionship =>
        val pointsPerPosition = pointsSystem.pointsForEdition(r.number)
        val results = r.edition.results
        results.zip(pointsPerPosition.padTo(results.size, 0)).flatMap {
          case (res, points) =>
            res.racer.racers.map { case name =>
              name -> Standing(name, r.number, res.position, res.kart, res.applyPenalty(points))
            }
        }.toMap
      case nr: NonReportedEditionInChampionship =>
        Map.empty[String, Standing]
    }
  }

  val orderedRacers: Seq[(String, Int)] = standingsByRace
    .flatMap(m => m.values)
    .toSeq
    .groupBy(_.racer)
    .map {
      case (r, standings) => (r, standings.foldLeft(0)(_ + _.points))
    }.toSeq
    .sortBy(_._2)
    .reverse

  val kartAssignments: Map[String, Seq[Int]] = {
    val resultsByRacer = editions.flatMap {
      case re: ReportedEditionInChampionship =>
        re.edition.results
      case _ => Seq()
    }.groupBy(_.racer.name)
    resultsByRacer.mapValues {
      case rrList => rrList.map(_.kart)
    }
  }

  def racerTotalPoints(racer: String): StandingResult = {
    orderedRacers.zipWithIndex.find {
      case ((r, _), _) => r == racer
    }.map {
      case ((_, points), pos) => StandingResult(pos + 1, points, 0)
    }.get
  }

  def racerPoints(racer: String): Seq[StandingResult] = {
    standingsByRace.map {
      case map =>
       map.get(racer) match {
         case Some(p) => StandingResult(p.position, p.points, p.kart)
         case None => StandingResult(0, 0, 0)
       }
    }
  }

  implicit def toJValue(pp: StandingResult): JValue = {
    ("position" -> pp.position) ~
    ("points" -> pp.points) ~
    ("kart" -> pp.kart)
  }

  def serialize(orderedRacerLinks: Seq[String]): String = {
    val json =
      ("racers" -> orderedRacerLinks) ~
      ("editions" -> editions.map(_.name)) ~
      ("data" -> orderedRacers.zipWithIndex.map {
        case ((r, p), pos) => racerPoints(r) :+ StandingResult(pos + 1, p, 0)
      })
    compact(render(json))
  }
}

case class Standing(racer: String, editionNumber: Int, position: Int, kart: Int, points: Int)
