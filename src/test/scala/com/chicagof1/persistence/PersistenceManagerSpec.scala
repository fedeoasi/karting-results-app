package com.chicagof1.persistence

import org.scalatest.{ShouldMatchers, FunSpec}
import com.chicagof1.model.{UserInfo, User}

class PersistenceManagerSpec extends FunSpec with ShouldMatchers {
  val pm = new TestPersistenceManager
  val firstUserInfo = UserInfo("first@gmail.com", "Hello World")
  val secondUserInfo = UserInfo("second@gmail.com", "Foo Bar")

  describe("Persistence Manager") {
    describe("Users") {
      it("should not find a non existing user") {
        pm.findUser("asd@asd.com") should be(None)
      }

      it("should store and find a user") {
        pm.saveUser(firstUserInfo)
        val actualUserOption = pm.findUser(firstUserInfo.email)
        assertUserInfo(firstUserInfo, actualUserOption)
      }

      it("should list users") {
        pm.saveUser(secondUserInfo)
        pm.listUsers().size should be(2)
      }

      describe("Logged In") {
        val thirdUserInfo = UserInfo("third@gmail.com", "Third User")

        it("creates a non existent user") {
          pm.loggedIn(thirdUserInfo)
          val optionalUser = pm.findUser(thirdUserInfo.email)
          assertUserInfo(thirdUserInfo, optionalUser)
        }

        it("marks the last login on an existing user") {
          val optionalOldUser = pm.findUser(thirdUserInfo.email)
          pm.loggedIn(thirdUserInfo)
          val optionalUser = pm.findUser(thirdUserInfo.email)
          assertUserInfo(thirdUserInfo, optionalUser)
          optionalUser.get.lastLogin.compareTo(optionalOldUser.get.lastLogin) >= 0 should be(right = true)
        }
      }
    }
  }

  private def assertUserInfo(expected: UserInfo, actual: Option[User]): Unit = {
    actual.isDefined should be(right = true)
    actual.get.email should be(expected.email)
    actual.get.fullName should be(expected.fullName)
  }
}
