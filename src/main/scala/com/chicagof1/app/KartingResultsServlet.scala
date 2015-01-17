package com.chicagof1.app

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.JsonDSL._
import com.chicagof1.data.DataManager
import com.chicagof1.parsing.{RacerWithStatsSerializer, VideoSerializer}
import org.scalatra._
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import com.chicagof1.facebook.FacebookInteractor
import com.chicagof1.metrics.MetricsHolder
import com.codahale.metrics.MetricRegistry
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.pac4j.j2e.util.UserUtils
import com.chicagof1.auth.AuthUtils
import com.chicagof1.model.Championship

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
    contentType = "text/html"
    ssp("index")
  }

  get("/races/:id") {
    contentType = "text/html"
    val raceId = params("id")
    val race = dataManager.getRaceById(raceId)
    jade("race", "race" -> race.get)
  }

  get("/api/races/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val raceId = params("id")
        val race = dataManager.getRaceById(raceId)
        val data = race.get.results.map {
          r => List[String](dataManager.racerLink(r.racer.name), r.position.toString, r.kart.toString, r.formattedTime)
        }
        val json = "aaData" -> data
        compact(render(json))
      }
    }
  }

  get("/editions") {
    contentType = "text/html"
    val editions = dataManager.editionsWithRaces
    jade("editions", "editionsWithRaces" -> editions)
  }

  get("/editions/:id") {
    contentType = "text/html"
    val edtionId = params("id")
    val editionWithRaces = dataManager.getEditionWithRacesById(edtionId)
    jade("edition", "editionWithRaces" -> editionWithRaces.get)
  }

  get("/events") {
    contentType = "text/html"
    jade("events")
  }

  get("/api/editions/:id") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val editionId = params("id")
        val edition = dataManager.getEditionWithRacesById(editionId)
        val data = edition.get.edition.results.map {
          r => List[String](dataManager.racerLink(r.racer.name), r.position.toString, r.kart.toString, r.formattedTime)
        }
        val json = "aaData" -> data
        compact(render(json))
      }
    }
  }

  get("/api/videos") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        vs.serializeVideos(dataManager.videos)
      }
    }
  }

  get("/standings") {
    redirect("/standings/2014")
  }

  get("/standings/:championshipId") {
    contentType = "text/html"
    val championshipId = params("championshipId")
    ssp("standings", "championshipId" -> championshipId)
  }

  get("/api/standings") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        serializedStandings(dataManager.currentChampionship)
      }
    }
  }

  get("/api/standings/:championshipId") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val championshipId = params("championshipId")
        dataManager.championship(championshipId) match {
          case Some(c) => serializedStandings(c)
          case None => NotFound(s"No championship with id $championshipId")
        }
      }
    }
  }

  private def serializedStandings(championship: Championship): String = {
    val standings = championship.standings
    val racerLinks = standings.orderedRacers.map {
      r => dataManager.racerLink(r._1)
    }
    standings.serialize(racerLinks)
  }

  get("/api/racers/:id") {
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
    contentType = "text/html"
    val racerId = racerIdString.toInt
    dataManager.getRacerById(racerId) match {
      case Some(racer) =>
        jade("racer", "racerId" -> racerId, "name" -> racer.name)
      case None =>
        NotFound()
    }
  }

  get("/user/profile") {
    val userProfile = UserUtils.getProfile(session)
    if(userProfile == null) {
      redirect(AuthUtils.redirectForAuthentication(request, response))
    } else {
      dataManager.racers.find(_.name == AuthUtils.fullName(userProfile)) match {
        case Some(r) => redirect(s"/racers/${r.id}")
        case None =>
          contentType = "text/html"
          jade("no-profile")
      }
    }
  }

  post("/api/reload") {
    dataManager.reload()
  }

  import com.chicagof1.Formatters._

  get("/api/events") {
    new AsyncResult() {
      override val is = Future {
        contentType = "application/json"
        val events = fb.chicagoF1Events().map { e =>
          val date = e.startTime.toString(prettyDateFormatter)
          val start = e.startTime.toString(cstTimeFormatter)
          val end = e.endTime.toString(cstTimeFormatter)
          val link = s"<a href=https://www.facebook.com/events/${e.id}/>${e.name}</a>"
          FacebookSerializedEvent(e.name, e.location, date, start, end, link)
        }
        write(EventsResponse(events))
      }
    }
  }

  get("/login") {
    Option(UserUtils.getProfile(session)) match {
      case Some(profile) => Ok("Already in")
      case None => redirect(AuthUtils.redirectForAuthentication(request, response))
    }
  }

  get("/logout") {
    UserUtils.logout(session)
    redirect("/")
  }

  error {
    case t: Throwable =>
      t.printStackTrace()
      if(contentType.contains("application/json")) {
        val trace = MessageAndTrace(t.getMessage, t.getStackTrace.map(_.toString).toList)
        InternalServerError(write(trace))
      } else {
        redirect("/")
      }
  }

  import MetricsHolder._

  val responses = metrics.timer(MetricRegistry.name(getClass, "responses"))
  val apiResponses = metrics.timer(MetricRegistry.name(getClass, "api-responses"))

  val skip = List("/js", "/css", "/images")
  val apiPrefixes = List("/api")

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    if(!skip.exists(request.getPathInfo.startsWith)) {
      serviceAndTimeRequest(request, response)
    } else {
      super.service(request, response)
    }
  }

  private def serviceAndTimeRequest(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val isApiRequest = apiPrefixes.exists(request.getPathInfo.startsWith)
    val context = if(isApiRequest) apiResponses.time() else responses.time()
    try {
      super.service(request, response)
    } finally {
      context.stop()
    }
  }

  override protected def isScalateErrorPageEnabled: Boolean = false
}

case class FacebookSerializedEvent(name: String, location: String, date: String, startTime: String, endTime: String, link: String)
case class EventsResponse(events: Seq[FacebookSerializedEvent])
case class MessageAndTrace(message: String, trace: List[String])