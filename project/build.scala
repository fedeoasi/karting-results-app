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
      resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      libraryDependencies ++= Seq(
        "org.scalatest"  %% "scalatest" % "1.9.1" % "test",
        "ch.qos.logback" % "logback-classic"     % "1.0.13",
        "org.jsoup" % "jsoup" % "1.7.2",
        "joda-time" % "joda-time" % "2.2",
        "org.joda"       % "joda-convert"        % "1.2",
        "org.clapper"    % "grizzled-slf4j_2.10" % "1.0.1",
        "javax.mail" % "mail" % "1.4.1",
        "org.apache.commons" % "commons-io" % "1.3.2",
        "org.scala-lang" % "scala-reflect" % "2.10.0",
        "com.github.tototoshi" %% "scala-csv" % "1.0.0-SNAPSHOT"
      )
    )
  )
}
