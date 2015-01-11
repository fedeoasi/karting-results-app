package com.chicagof1.auth

import org.pac4j.j2e.configuration.ClientsFactory
import org.pac4j.core.client.Clients
import org.pac4j.oauth.client.FacebookClient
import com.chicagof1.facebook.FacebookInteractor
import com.chicagof1.app.AppLocation

class MyClientsFactory extends ClientsFactory {
  override def build(): Clients = {
    val credentials = FacebookInteractor.facebookCredentials
    val facebookClient = new FacebookClient(credentials.appId, credentials.secret)
    new Clients(s"http://${AppLocation.currentLocation}/callback", facebookClient)
  }
}
