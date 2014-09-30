package com.chicagof1.app

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.JsonDSL._
import com.chicagof1.data.DataManager
import com.chicagof1.parsing.{RacerWithStatsSerializer, VideoSerializer}
import org.scalatra.{NotFound, AsyncResult, FutureSupport}
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import com.chicagof1.facebook.FacebookInteractor

class KartingResultsServlet(dataManager: DataManager) extends KartingResultsAppStack with FutureSupport {
  implicit val formats = org.json4s.DefaultFormats
  val vs = new VideoSerializer
  val rwss = new RacerWithStatsSerializer
  val fb = new FacebookInteractor

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
        val data = race.get.results.map {
          r => List[String](dataManager.racerLink(r.name), r.position.toString, r.kart.toString, r.formattedTime)
        }
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

  get("/events") {
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        jade("events")
      }
    }
  }

  get("/data/editions/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val editionId = params("id")
        val edition = dataManager.getEditionWithRacesById(editionId)
        val data = edition.get.edition.results.map {
          r => List[String](dataManager.racerLink(r.name), r.position.toString, r.kart.toString, r.formattedTime)
        }
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
        val standings = dataManager.currentChampionship.standings
        val racerLinks = standings.orderedRacers.map {
          r => dataManager.racerLink(r._1)
        }
        standings.serialize(racerLinks)
      }
    }
  }

  get("/data/racers/:id") {
    val racerIdString = params("id")
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val racerId = racerIdString.toInt
        dataManager.getRacerById(racerId) match {
          case Some(racer) =>
            val racerWithStats = dataManager.racerStatsFor(racer.name)
            rwss.serializeRacerWithStats(racerWithStats)
          case None =>
            NotFound()
        }
      }
    }
  }

  get("/racers/:id") {
    val racerIdString = params("id")
    new AsyncResult() {
      override val is = Future {
        contentType = "text/html"
        val racerId = racerIdString.toInt
        dataManager.getRacerById(racerId) match {
          case Some(racer) =>
            ssp("racer", "racerId" -> racerId, "name" -> racer.name)
          case None =>
            NotFound()
        }
      }
    }
  }

  post("/data/reload") {
    dataManager.reload()
  }

  import com.chicagof1.Formatters._

  get("/api/events") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val events = fb.chicagoF1Events().map { e =>
          val date = e.startTime.toString(prettyDateFormatter)
          val start = e.startTime.toString(timeFormatter)
          val end = e.endTime.toString(timeFormatter)
          val link = s"<a href=https://www.facebook.com/events/${e.id}/>${e.name}</a>"
          FacebookSerializedEvent(e.name, e.location, date, start, end, link)
        }
        write(EventsResponse(events))
      }
    }
  }
}

case class FacebookSerializedEvent(name: String, location: String, date: String, startTime: String, endTime: String, link: String)
case class EventsResponse(events: Seq[FacebookSerializedEvent])
