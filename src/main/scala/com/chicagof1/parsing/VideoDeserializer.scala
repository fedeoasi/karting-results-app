package com.chicagof1.parsing

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import com.chicagof1.model.Video

class VideoDeserializer {
  implicit lazy val formats = DefaultFormats

  def deserializeVideo(json: String): Video = {
    val ast = parse(json)
    ast.extract[Video]
  }

  def deserializeVideos(json: String): Seq[Video] = {
    val ast = parse(json)
    (ast \ "videos").extract[Seq[Video]]
  }
}
