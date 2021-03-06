package com.chicagof1.persistence

import scala.slick.driver.JdbcDriver
import com.github.tototoshi.slick.GenericJodaSupport
import com.chicagof1.model.User
import org.joda.time.DateTime

trait Profile {
  val driver: JdbcDriver
}

trait DBComponent {
  this: Profile =>
  import driver.simple._

  object JodaSupport extends GenericJodaSupport(driver)

  lazy val onDeleteAction: ForeignKeyAction = ForeignKeyAction.Cascade
}

trait ChicagoF1DbComponent extends DBComponent {
  this: Profile =>

  import driver.simple._
  import JodaSupport._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc, O.NotNull)
    def email = column[String]("EMAIL", O.NotNull)
    def fullName = column[String]("FULL_NAME")
    def firstLogin = column[DateTime]("FIRST_LOGIN")
    def lastLogin = column[DateTime]("LAST_LOGIN")
    def * = (email, fullName, firstLogin, lastLogin, id.?) <> (User.tupled, User.unapply)

    def emailIdx = index("EMAIL_IDX", email, unique = true)
  }

  object users extends TableQuery(new Users(_))
}

class ChicagoF1DAL(override val driver: JdbcDriver) extends ChicagoF1DbComponent with Profile