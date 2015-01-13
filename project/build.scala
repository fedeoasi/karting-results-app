import sbt._
import Keys._
import org.scalatra.sbt._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._


object KartingApplicationBuild extends Build {
  val Organization = "com.chicagof1"
  val Name = "Karting Results Application"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.4"
  val ScalatraVersion = "2.2.2"
  val Pac4JVersion = "1.6.0"

  def containerConf = config("container")

  lazy val project = Project (
    "karting-application",
    file("."),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      scalacOptions ++= Seq("-feature"),
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      unmanagedResourceDirectories in Runtime += file("output"),
      unmanagedClasspath in Runtime += file("output"),
      fullClasspath in containerConf += file("output"),
        libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatest"  %% "scalatest" % "2.1.6" % "test",
        "org.apache.commons" % "commons-io" % "1.3.2",
        "org.json4s" %% "json4s-jackson" % "3.2.10",
        "ch.qos.logback" % "logback-classic"     % "1.1.2",
        "org.jsoup" % "jsoup" % "1.7.3",
        "joda-time" % "joda-time" % "2.3",
        "io.dropwizard.metrics" % "metrics-core" % "3.1.0",
        "io.dropwizard.metrics" % "metrics-servlets" % "3.1.0",
        "com.github.nscala-time" %% "nscala-time" % "1.0.0",
        "org.joda"       % "joda-convert"        % "1.6",
        "org.clapper"    %% "grizzled-slf4j" % "1.0.2",
        "javax.mail" % "mail" % "1.4.7",
        "org.scala-lang" % "scala-reflect" % ScalaVersion,
        "org.scala-lang" % "scala-compiler" % ScalaVersion,
        "com.github.tototoshi" %% "scala-csv" % "1.0.0",
        "org.eclipse.jetty" % "jetty-webapp" % "9.1.5.v20140505" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "org.scala-lang.modules" %% "scala-async" % "0.9.1",
        "org.facebook4j" % "facebook4j-core" % "2.1.0",
        "org.pac4j" % "j2e-pac4j" % "1.0.4",
        "org.pac4j" % "pac4j-oauth" % Pac4JVersion,
        "com.typesafe.slick" %% "slick" % "2.1.0",
        "org.xerial" % "sqlite-jdbc" % "3.8.7",
        "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
        "com.h2database" % "h2" % "1.4.181"
        ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )
}
