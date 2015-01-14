package com.chicagof1.persistence

import com.chicagof1.model.{UserInfo, User}

trait PersistenceManager {
  def saveUser(user: UserInfo): Unit
  def findUser(email: String): Option[User]
  def listUsers(): Seq[User]
  def loggedIn(user: UserInfo): Unit
}

