package com.chicagof1.auth

import org.pac4j.j2e.configuration.ClientsConfiguration
import org.pac4j.oauth.client.FacebookClient
import org.pac4j.core.context.J2EContext
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.pac4j.core.profile.CommonProfile

object AuthUtils {
  def redirectForAuthentication(req: HttpServletRequest, res: HttpServletResponse) = {
    val facebookClient = ClientsConfiguration.getClients.findClient("FacebookClient").asInstanceOf[FacebookClient]
    val webContext = new J2EContext(req, res)
    facebookClient.getRedirectAction(webContext, false, false).getLocation
  }

  def fullName(profile: CommonProfile): String = profile.getAttribute("name").toString
}
