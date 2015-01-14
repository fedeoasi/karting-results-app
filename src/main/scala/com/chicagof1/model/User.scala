package com.chicagof1.model

import org.joda.time.DateTime

case class User(email: String, fullName: String, firstLogin: DateTime, lastLogin: DateTime, id: Option[Int])
case class UserInfo(email: String, fullName: String)
