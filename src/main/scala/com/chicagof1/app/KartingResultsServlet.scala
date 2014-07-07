package com.chicagof1.app

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import com.chicagof1.data.DataManager
import com.chicagof1.parsing.VideoSerializer
import org.scalatra.{AsyncResult, FutureSupport}
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

class KartingResultsServlet(dataManager: DataManager) extends KartingResultsAppStack with FutureSupport {
  val vs = new VideoSerializer

  override protected implicit def executor: ExecutionContext = global

  get("/trace") {
    contentType = "text/html"
    val traceList = Thread.getAllStackTraces.asScala.toList
    val sortedTraceList = traceList.sortBy(_._1.getName.toLowerCase)
    jade("trace", "threadsAndTraces" -> sortedTraceList)
  }

  get("/") {
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        ssp("index")
      }
    }
  }

  get("/races/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        val raceId = params("id")
        val race = dataManager.getRaceById(raceId)
        jade("race", "race" -> race.get)
      }
    }
  }

  get("/data/races/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val raceId = params("id")
        val race = dataManager.getRaceById(raceId)
        val data = race.get.results.map(r => List[String](r.name, r.position.toString, r.kart.toString, r.formattedTime))
        val json = "aaData" -> data
        compact(render(json))
      }
    }
  }

  get("/editions") {
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        val editions = dataManager.editionsWithRaces
        jade("editions", "editionsWithRaces" -> editions)
      }
    }
  }

  get("/editions/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        val edtionId = params("id")
        val editionWithRaces = dataManager.getEditionWithRacesById(edtionId)
        jade("edition", "editionWithRaces" -> editionWithRaces.get)
      }
    }
  }

  get("/data/editions/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val editionId = params("id")
        val edition = dataManager.getEditionWithRacesById(editionId)
        val data = edition.get.edition.results.map(r => List[String](r.name, r.position.toString, r.kart.toString, r.formattedTime))
        val json = "aaData" -> data
        compact(render(json))
      }
    }
  }

  get("/data/videos") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        vs.serializeVideos(dataManager.videos)
      }
    }
  }

  get("/data/standings") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        dataManager.currentChampionship.standings.serialize
      }
    }
  }
}
