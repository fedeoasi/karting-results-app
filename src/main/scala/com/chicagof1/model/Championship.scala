package com.chicagof1.model

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.JValue
import scala.language.implicitConversions

case class Championship(name: String, editions: Seq[EditionInChampionship], pointsSystem: PointsSystem) {
  lazy val standings: Standings = new Standings(editions, pointsSystem)
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

case class PositionAndPoints(position: Int, points: Int)

class ChicagoF1PointsSystem(n: Int) extends PointsSystem {
  val basePoints = 20.to(1, -1).toSeq

  def pointsForEdition(number: Int): Seq[Int] = {
    if(number == 1) {
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

class Standings(editions: Seq[EditionInChampionship], pointsSystem: PointsSystem) {
  val standingsByRace: Seq[Map[String, Standing]] = {
    editions.map {
      case r: ReportedEditionInChampionship =>
        val pointsPerPosition = pointsSystem.pointsForEdition(r.number)
        val results = r.edition.results
        results.zip(pointsPerPosition.padTo(results.size, 0)).map {
          case (res, points) => res.racer.name -> Standing(res.racer.name, r.number, res.position, res.applyPenalty(points))
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

  def racerTotalPoints(racer: String): PositionAndPoints = {
    orderedRacers.zipWithIndex.find {
      case ((r, _), _) => r == racer
    }.map {
      case ((_, points), pos) => PositionAndPoints(pos + 1, points)
    }.get
  }

  def racerPoints(racer: String): Seq[PositionAndPoints] = {
    standingsByRace.map {
      case map =>
       map.get(racer) match {
         case Some(p) => PositionAndPoints(p.position, p.points)
         case None => PositionAndPoints(0, 0)
       }
    }
  }

  implicit def toJValue(pp: PositionAndPoints): JValue = {
    ("position" -> pp.position) ~
    ("points" -> pp.points)
  }

  def serialize(orderedRacerLinks: Seq[String]): String = {
    val json =
      ("racers" -> orderedRacerLinks) ~
      ("editions" -> editions.map(_.name)) ~
      ("data" -> orderedRacers.zipWithIndex.map {
        case ((r, p), pos) => racerPoints(r) :+ PositionAndPoints(pos + 1, p)
      })
    compact(render(json))
  }
}

case class Standing(racer: String, editionNumber: Int, position: Int, points: Int)
