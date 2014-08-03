package com.chicagof1.parsing

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import com.chicagof1.model.Video

class VideoDeserializer {
  import ModelSerialization._

  def deserializeVideo(json: String): Video = {
    val ast = parse(json)
    ast.extract[Video]
  }

  def deserializeVideos(json: String): Seq[Video] = {
    val ast = parse(json)
    (ast \ "videos").extract[Seq[Video]]
  }
}

class VideoSerializer {
  import ModelSerialization._

  def serializeVideo(video: Video): String = {
    write(video)
  }

  def serializeVideos(videos: Seq[Video]): String = {
    "{\"videos\":" + write(videos) + "}"
  }
}
