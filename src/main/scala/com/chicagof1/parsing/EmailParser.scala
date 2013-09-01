package com.chicagof1.parsing

import javax.mail.internet.{MimeMultipart, MimeMessage}
import javax.mail.{BodyPart, Session}
import java.util.Properties
import java.io.FileInputStream

class EmailParser {
  def parseEmail(path: String): MimeMessage = {
    val session = Session.getDefaultInstance(new Properties());
    val input = new FileInputStream(path);
    new MimeMessage(session, input);
  }

  def getFirstBodyPartContentAsString(message: MimeMessage): String = {
    message
      .getContent.asInstanceOf[MimeMultipart]
      .getBodyPart(0)
      .getContent.asInstanceOf[String]
  }

  def getFirstHtmlBodyPartContentAsString(message: MimeMessage): String = {
    val multiPart = message
      .getContent.asInstanceOf[MimeMultipart]
    var index = 0
    while(index < multiPart.getCount) {
      val part: BodyPart = multiPart.getBodyPart(index)
      if(part.getContentType.contains("multipart/alternative")) {
        val subMultiPart = part.getContent.asInstanceOf[MimeMultipart]
        return subMultiPart.getBodyPart(1).getContent.asInstanceOf[String]
      }
      index += 1
    }
    ""
  }
}
