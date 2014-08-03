package com.chicagof1.parsing

import org.json4s.jackson.Serialization._
import com.chicagof1.data.RacerWithStats

class RacerWithStatsSerializer {
  import ModelSerialization._

  def serializeRacerWithStats(r: RacerWithStats): String = {
    write(r)
  }
}
