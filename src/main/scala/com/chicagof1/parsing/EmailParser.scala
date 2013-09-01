package com.chicagof1.parsing

import javax.mail.internet.{MimeMultipart, MimeMessage}
import javax.mail.Session
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
}
