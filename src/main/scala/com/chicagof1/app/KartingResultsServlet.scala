package com.chicagof1.app

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import com.chicagof1.data.DataManager

class KartingResultsServlet(dataManager: DataManager) extends KartingResultsAppStack {

  get("/") {
    contentType = "text/html"
    jade("index")
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

  get("/editions") {
    contentType = "text/html"
    val editions = dataManager.editions
    jade("editions", "editions" -> editions)
  }

  get("/editions/:id") {
    contentType = "text/html"
    val edtionId = params("id")
    val edition = dataManager.getEditionById(edtionId)
    jade("edition", "edition" -> edition.get)
  }

  get("/data/editions/:id") {
    contentType = "application/json"
    val editionId = params("id")
    val edition = dataManager.getEditionById(editionId)
    val data = edition.get.results.map(r => List[String](r.name, r.position.toString, r.kart.toString, r.formattedTime))
    val json = "aaData" -> data
    compact(render(json))
  }
}
