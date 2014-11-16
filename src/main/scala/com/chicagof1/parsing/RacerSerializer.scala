package com.chicagof1.parsing

import com.chicagof1.model.SingleRacerDao
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._

class RacerDeserializer {
  import ModelSerialization._

  def deserializeRacerDao(json: String): SingleRacerDao = {
    val ast = parse(json)
    ast.extract[SingleRacerDao]
  }

  def deserializeRacerDaos(json: String): Seq[SingleRacerDao] = {
    val ast = parse(json)
    (ast \ "racers").extract[Seq[SingleRacerDao]]
  }
}

class RacerSerializer {
  import ModelSerialization._

  def serializeRacerDao(racer: SingleRacerDao): String = {
    write(racer)
  }

  def serializeRacerDaos(racers: Seq[SingleRacerDao]): String = {
    "{\"racers\":" + write(racers) + "}"
  }
}
