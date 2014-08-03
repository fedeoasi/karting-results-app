package com.chicagof1.data

import com.chicagof1.model._
import com.chicagof1.model.Edition
import com.chicagof1.model.Racer
import com.chicagof1.model.Video
import com.chicagof1.model.Race

case class RacerWithStats(racer: Racer,
                          currentStandingsPosition: Int,
                          currentPoints: Int,
                          editionWinCount: Int,
                          raceWinCount: Int,
                          avgEditionPosition: Double,
                          videosCount: Int)

class StatsCalculator {
  def racerStatsFor(racer: Racer, races: List[Race], editions: List[Edition], videos: List[Video], currentStandings: Standings): RacerWithStats = {
    val standingsPosition = currentStandings.racerTotalPoints(racer.name)
    val editionPositions = editions.flatMap {
      _.results
        .find(_.name == racer.name)
        .map(_.position)
    }

    RacerWithStats(racer,
      standingsPosition.position,
      standingsPosition.points,
      editions.count(_.winner == racer.name),
      races.count(_.winner == racer.name),
      avg(editionPositions),
      videos.count(_.racer == racer.name))
  }

  private def avg(values: Seq[Int]): Double = {
    if(values.size == 0) 0.0
    else values.sum.toDouble / values.size.toDouble
  }
}
