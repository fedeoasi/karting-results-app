package com.chicagof1.persistence

import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.driver.SQLiteDriver

class ProdPersistenceManager(dbName: String) extends BasePersistenceManager {
  val database = Database.forURL(
    "jdbc:sqlite:%s.db" format dbName,
    driver = "org.sqlite.JDBC")
  override val dal: ChicagoF1DAL = new ChicagoF1DAL(SQLiteDriver)

  initializeDatabase()
}
