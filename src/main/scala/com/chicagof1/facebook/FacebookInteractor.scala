package com.chicagof1.facebook

import org.joda.time.{DateTime, LocalDate}
import facebook4j._
import facebook4j.auth.AccessToken
import scala.collection.JavaConverters._
import java.util.Date

case class FacebookEvent(id: String, name: String, startTime: DateTime, endTime: DateTime, location: String)

class FacebookInteractor {
  private val facebook = {
    val appId = System.getenv("CHI_F1_APP_ID")
    val appSecret = System.getenv("CHI_F1_APP_SECRET")
    val accessKey = s"$appId|$appSecret"
    val fb = new FacebookFactory().getInstance()
    fb.setOAuthAppId(appId, appSecret)
    fb.setOAuthAccessToken(new AccessToken(accessKey, null))
    fb
  }

  private val chicagoF1PageId = "460538837312267"

  def retrieveChicagoF1PageId(): String = {
    facebook.getPage("https://www.facebook.com/ChicagoF1").getId
  }

  def chicagoF1Events(): Seq[FacebookEvent] = {
    val options = new Reading().since(LocalDate.now.minusMonths(12).toDate)
    val events = facebook.events().getEvents(chicagoF1PageId, options)
    events.iterator().asScala.toList.map { e =>
      FacebookEvent(e.getId, e.getName, dateTime(e.getStartTime), dateTime(e.getEndTime), e.getLocation)
    }
  }

  private def dateTime(date: Date): DateTime = new DateTime(date)
}
