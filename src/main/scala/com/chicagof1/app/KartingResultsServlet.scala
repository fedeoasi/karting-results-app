package com.chicagof1.app

import org.scalatra._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import com.chicagof1.data.DataManager

class KartingResultsServlet(dataManager: DataManager) extends KartingResultsAppStack {

  get("/") {
    <html>
      <body>
        <h1>Welcome to the Chicago F1 Karting Results App</h1>
        <a href="editions">Editions</a>
        <a href="races">Races</a>
      </body>
    </html>
  }

  get("/races") {
    contentType = "text/html"
    val raceIds = dataManager.races.map(_.raceId)
    jade("races", "raceIds" -> raceIds)
  }

  get("/races/:id") {
    contentType = "text/html"
    val raceId = params("id")
    val race = dataManager.getRaceById(raceId)
    jade("race", "race" -> race.get)
  }

  get("/data/races/:id") {
    contentType = "application/json"
    val raceId = params("id")
    val race = dataManager.getRaceById(raceId)
    val data = race.get.results.map(r => List[String](r.name, r.position.toString, r.kart.toString, r.formattedTime))
    val json = "aaData" -> data
    compact(render(json))
  }

  get("/data/races") {
    contentType = "application/json"
    val data = dataManager.races.map(r => List[String](r.date.toString, "", "", ""))
    val json = "aaData" -> data
    compact(render(json))
  }
}
