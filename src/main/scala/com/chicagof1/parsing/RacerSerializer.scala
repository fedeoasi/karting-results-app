package com.chicagof1.parsing

import com.chicagof1.model.RacerDao
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._

class RacerDeserializer {
  import ModelSerialization._

  def deserializeRacerDao(json: String): RacerDao = {
    val ast = parse(json)
    ast.extract[RacerDao]
  }

  def deserializeRacerDaos(json: String): Seq[RacerDao] = {
    val ast = parse(json)
    (ast \ "racers").extract[Seq[RacerDao]]
  }
}

class RacerSerializer {
  import ModelSerialization._

  def serializeRacerDao(racer: RacerDao): String = {
    write(racer)
  }

  def serializeRacerDaos(racers: Seq[RacerDao]): String = {
    "{\"racers\":" + write(racers) + "}"
  }
}
