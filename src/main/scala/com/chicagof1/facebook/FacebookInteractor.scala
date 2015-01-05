package com.chicagof1.facebook

import org.joda.time.{DateTime, LocalDate}
import facebook4j._
import facebook4j.auth.AccessToken
import scala.collection.JavaConverters._
import java.util.Date

case class FacebookEvent(id: String, name: String, startTime: DateTime, endTime: DateTime, location: String)

case class FacebookAppCredentials(appId: String, secret: String) {
  def accessKey = s"$appId|$secret"
}

class FacebookInteractor {
  private val facebook = {
    val credentials = FacebookInteractor.facebookCredentials
    val fb = new FacebookFactory().getInstance()
    fb.setOAuthAppId(credentials.appId, credentials.secret)
    fb.setOAuthAccessToken(new AccessToken(credentials.accessKey, null))
    fb
  }

  private val chicagoF1PageId = "460538837312267"

  def retrieveChicagoF1PageId(): String = {
    facebook.getPage("https://www.facebook.com/ChicagoF1").getId
  }

  def chicagoF1Events(): Seq[FacebookEvent] = {
    val options = new Reading().since(LocalDate.now.minusMonths(4).toDate)
    val events = facebook.events().getEvents(chicagoF1PageId, options)
    events.iterator().asScala.toList.map { e =>
      FacebookEvent(e.getId, e.getName, dateTime(e.getStartTime), dateTime(e.getEndTime), e.getLocation)
    }
  }

  private def dateTime(date: Date): DateTime = new DateTime(date)
}

object FacebookInteractor {
  lazy val facebookCredentials: FacebookAppCredentials = {
    if(true) {
      val devAppId = System.getenv("CHI_F1_DEV_APP_ID")
      val devAppSecret = System.getenv("CHI_F1_DEV_APP_SECRET")
      FacebookAppCredentials(devAppId, devAppSecret)
    } else {
      val appId = System.getenv("CHI_F1_APP_ID")
      val appSecret = System.getenv("CHI_F1_APP_SECRET")
      FacebookAppCredentials(appId, appSecret)
    }
  }
}