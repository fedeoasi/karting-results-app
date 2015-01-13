package com.chicagof1.persistence

import scala.slick.driver.JdbcDriver
import com.github.tototoshi.slick.GenericJodaSupport
import com.chicagof1.model.User

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
  //import JodaSupport._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc, O.NotNull)
    def email = column[String]("EMAIL", O.NotNull)
    def fullName = column[String]("FULL_NAME")
    def * = (email, fullName, id.?) <> (User.tupled, User.unapply)
  }

  object users extends TableQuery(new Users(_))
}

class ChicagoF1DAL(override val driver: JdbcDriver) extends ChicagoF1DbComponent with Profile