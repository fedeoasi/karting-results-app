import sbt._
import Keys._

object KartingApplicationBuild extends Build {
  val Organization = "com.chicagof1"
  val Name = "Karting Results Application"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.2"

  lazy val project = Project (
    "karting-application",
    file("."),
    settings = Defaults.defaultSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime"
      )
    )
  )
}
