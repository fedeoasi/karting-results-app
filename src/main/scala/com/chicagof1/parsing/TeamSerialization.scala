package com.chicagof1.parsing

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import com.chicagof1.model.Team

class TeamDeserializer {
  import ModelSerialization._

  def deserializeTeam(json: String): Team = {
    val ast = parse(json)
    ast.extract[Team]
  }

  def deserializeTeams(json: String): Seq[Team] = {
    val ast = parse(json)
    (ast \ "teams").extract[Seq[Team]]
  }
}

class TeamSerializer {
  import ModelSerialization._

  def serializeTeam(team: Team): String = {
    write(team)
  }

  def serializeTeams(teams: Seq[Team]): String = {
    "{\"videos\":" + write(teams) + "}"
  }
}
