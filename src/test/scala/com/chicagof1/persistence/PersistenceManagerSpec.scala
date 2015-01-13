package com.chicagof1.persistence

import org.scalatest.{ShouldMatchers, FunSpec}
import com.chicagof1.model.User

class PersistenceManagerSpec extends FunSpec with ShouldMatchers {
  val pm = new TestPersistenceManager
  val firstUser = User("first@gmail.com", "Hello World", Some(1))
  val secondUser = User("second@gmail.com", "Foo Bar", Some(2))

  describe("Persistence Manager") {
    describe("Users") {
      it("should not find a non existing user") {
        pm.findUser("asd@asd.com") should be(None)
      }

      it("should store and find a user") {
        pm.saveUser(firstUser)
        pm.findUser(firstUser.email) should be(Some(firstUser))
      }

      it("should list users") {
        pm.saveUser(secondUser)
        pm.listUsers() should be(Seq(firstUser, secondUser))
      }
    }
  }
}
