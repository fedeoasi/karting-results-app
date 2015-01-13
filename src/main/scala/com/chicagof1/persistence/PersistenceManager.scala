package com.chicagof1.persistence

import com.chicagof1.model.User

trait PersistenceManager {
  def saveUser(user: User): Unit
  def findUser(email: String): Option[User]
  def listUsers(): Seq[User]
}

