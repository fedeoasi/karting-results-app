package com.chicagof1.parsing

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.chicagof1.model.Video

class VideoParsingSpec extends FunSpec with ShouldMatchers {
  val vd = new VideoDeserializer

  describe("Video Parsing") {
    it("should parse a video") {
      val videoJson = """{"id": "WhOIM936iqo", "racer": "STRIKER", "date": "2013-07-17","location":"Addison K1"}"""
      vd.deserializeVideo(videoJson) should be(Video("WhOIM936iqo", "STRIKER", "2013-07-17", "Addison K1"))
    }

    it("should parse a seq of videos") {
      val videoJson =
        """
          |{"videos": [
          |    {"id": "WhOIM936iqo", "racer": "STRIKER", "date": "2013-07-17","location":"Chicago Race Factory"},
          |    {"id": "tA0uA1nG7gE", "racer": "meesa", "date": "2013-11-13", "location":"Addison K1"}
          |]}
        """.stripMargin
      vd.deserializeVideos(videoJson) should be(
        Seq(
          Video("WhOIM936iqo", "STRIKER", "2013-07-17", "Chicago Race Factory"),
          Video("tA0uA1nG7gE", "meesa", "2013-11-13", "Addison K1")
        ))
    }
  }

  val vs = new VideoSerializer

  describe("Video Serialization") {
    it("should serialize a video") {
      val video = Video("WhOIM936iqo", "STRIKER", "2013-07-17", "Addison K1")
      val json = """{"id":"WhOIM936iqo","racer":"STRIKER","date":"2013-07-17","location":"Addison K1"}"""
      vs.serializeVideo(video) should be(json)
    }

    it("should parse a seq of videos") {
      val videos = Seq[Video](
        Video("WhOIM936iqo", "STRIKER", "2013-07-17", "Melrose Park"),
        Video("tA0uA1nG7gE", "meesa", "2013-11-13", "Melrose Park")
      )
      val videoJson =
        """{"videos":[{"id":"WhOIM936iqo","racer":"STRIKER","date":"2013-07-17","location":"Melrose Park"},{"id":"tA0uA1nG7gE","racer":"meesa","date":"2013-11-13","location":"Melrose Park"}]}"""
      vs.serializeVideos(videos) should be(videoJson)
    }
  }
}
