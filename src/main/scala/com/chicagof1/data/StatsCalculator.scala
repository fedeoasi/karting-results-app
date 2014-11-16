package com.chicagof1.data

import com.chicagof1.model._
import com.chicagof1.model.Edition
import com.chicagof1.model.SingleRacer
import com.chicagof1.model.Video
import com.chicagof1.model.Race

case class RacerWithStats(racer: SingleRacer,
                          currentStandingsPosition: Int,
                          currentPoints: Int,
                          editionWinCount: Int,
                          raceWinCount: Int,
                          avgEditionPosition: Double,
                          editionPosHistogram: Seq[Int],
                          videosCount: Int)

class StatsCalculator {
  def racerStatsFor(racer: SingleRacer, races: List[Race], editions: List[Edition], videos: List[Video], currentStandings: Standings): RacerWithStats = {
    val standingsPosition = currentStandings.racerTotalPoints(racer.name)
    val editionPositions = editions.flatMap {
      _.results
        .find(_.racer.name == racer.name)
        .map(_.position)
    }
    val groupedPositions: Map[Int, List[Int]] = editionPositions.groupBy(_.toInt)
    val positionCounts = 1 to 24 map {
      i => groupedPositions.get(i).fold(0)(_.size)
    }
    RacerWithStats(racer,
      standingsPosition.position,
      standingsPosition.points,
      editions.count(_.winner == racer.name),
      races.count(_.winner == racer.name),
      avg(editionPositions),
      positionCounts,
      videos.count(_.racer == racer.name))
  }

  private def avg(values: Seq[Int]): Double = {
    if(values.size == 0) 0.0
    else values.sum.toDouble / values.size.toDouble
  }
}
