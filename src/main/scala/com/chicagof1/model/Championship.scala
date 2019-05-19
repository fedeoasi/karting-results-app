package com.chicagof1.model

import com.chicagof1.links.LinkBuilder
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.JValue
import scala.language.implicitConversions

case class Championship(id: String, editions: Seq[EditionInChampionship], pointsSystem: PointsSystem, teams: List[Team]) {
  lazy val standings: Standings = new Standings(editions, pointsSystem, teams)
  def name: String = s"Chicago F1 $id"
  def summary: ChampionshipSummary = {
    val leader = standings.orderedRacers.headOption.map(_._1).getOrElse("")
    val size = editions.size
    val completedSize = editions.count(_.happened)
    val racerCount = standings.orderedRacers.size
    ChampionshipSummary(id, name, leader, completedSize, size, racerCount, s"/standings/$id")
  }
}

trait EditionInChampionship {
  def number: Int
  def name: String
  def happened: Boolean
}

case class ReportedEditionInChampionship(number: Int, name: String, edition: Edition) extends EditionInChampionship {
  override def happened: Boolean = true
}
case class NonReportedEditionInChampionship(number: Int, name: String) extends EditionInChampionship {
  override def happened: Boolean = false
}

trait PointsSystem {
  def pointsForEdition(number: Int): Seq[Int]
}

case class StandingResult(position: Int, points: Int, kart: Int, removed: Boolean)

class ChicagoF12014PointsSystem() extends PointsSystem {
  val basePoints = 20.to(1, -1).toSeq

  def pointsForEdition(number: Int): Seq[Int] = {
    if(number == 1 || number == 11) {
      basePoints.map(_ * 2)
    } else if(number > 1) {
      basePoints
    } else {
      Seq.empty[Int]
    }
  }
}

class ChicagoF12015PointsSystem() extends PointsSystem {
  val basePoints = 20.to(1, -1).toSeq

  def pointsForEdition(number: Int): Seq[Int] = {
    require(number > 0)
    basePoints
  }
}

class FormulaOnePointsSystem extends PointsSystem {
  //noinspection ScalaStyle
  val basePoints = Seq(25, 18, 15, 12, 10, 8, 6, 4, 2, 1)

  def pointsForEdition(number: Int): Seq[Int] = {
    require(number > 0)
    basePoints
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
              name -> Standing(name, r.number, res.position, res.kart, res.applyPenalty(points), removed = false)
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
      case (r, standings) => (r, standings.foldLeft(0) { case (a, s) =>
        if (s.removed) a else a + s.points
      })
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
      case ((_, points), pos) => StandingResult(pos + 1, points, 0, false)
    }.getOrElse(StandingResult(0, 0, 0, false))
  }

  def racerPoints(racer: String): Seq[StandingResult] = {
    standingsByRace.map {
      case map =>
       map.get(racer) match {
         case Some(p) => StandingResult(p.position, p.points, p.kart, p.removed)
         case None => StandingResult(0, 0, 0, false)
       }
    }
  }

  implicit def toJValue(pp: StandingResult): JValue = {
    ("position" -> pp.position) ~
    ("points" -> pp.points) ~
    ("removed" -> pp.removed) ~
    ("kart" -> pp.kart)
  }

  def serialize(orderedRacerLinks: Seq[String]): String = {
    val json =
      ("racers" -> orderedRacerLinks) ~
      ("editions" -> editions.map(e => LinkBuilder.editionLink(e))) ~
      ("data" -> orderedRacers.zipWithIndex.map {
        case ((r, p), pos) => racerPoints(r) :+ StandingResult(pos + 1, p, 0, false)
      })
    compact(render(json))
  }
}

case class Standing(racer: String, editionNumber: Int, position: Int, kart: Int, points: Int, removed: Boolean)

case class ChampionshipSummary(id: String, name: String, leader: String, completedSize: Int, size: Int, racerCount: Int, link: String)