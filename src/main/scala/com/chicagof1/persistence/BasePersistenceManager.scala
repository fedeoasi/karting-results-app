package com.chicagof1.persistence

import grizzled.slf4j.Logging
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.driver.H2Driver
import com.chicagof1.model.{UserInfo, User}
import scala.slick.jdbc.meta.MTable
import java.util.UUID
import org.joda.time.DateTime

abstract class BasePersistenceManager extends PersistenceManager with Logging {
  val database: Database
  val dal: ChicagoF1DAL

  import dal._
  import driver.simple._

  override def saveUser(user: UserInfo): Unit = {
    database withSession { implicit s =>
      val now = DateTime.now
      val userToPersist = User(user.email, user.fullName, now, now, None)
      users.insert(userToPersist)
    }
  }

  override def findUser(email: String): Option[User] = {
    database withSession { implicit s =>
      users.filter(_.email === email).list.headOption
    }
  }

  override def listUsers(): Seq[User] = {
    database withSession { implicit s =>
      users.list.toSeq
    }
  }

  override def loggedIn(user: UserInfo): Unit = {
    saveUser(user)
  }

  def initializeDatabase() {
    database withSession { implicit s =>
      if(!MTable.getTables.list.exists(_.name.name == users.shaped.value.tableName)) {
        info(users.ddl.createStatements.mkString("\n"))
        users.ddl.create
      }
    }
    logger.info("The database has been initialized")
  }
}



class TestPersistenceManager extends BasePersistenceManager {
  val database = Database.forURL(s"jdbc:h2:mem:chicagof1" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1",
    driver = "org.h2.Driver")
  override val dal: ChicagoF1DAL = new ChicagoF1DAL(H2Driver)

  initializeDatabase()
}