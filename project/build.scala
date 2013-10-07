import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._


object KartingApplicationBuild extends Build {
  val Organization = "com.chicagof1"
  val Name = "Karting Results Application"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.0"
  val ScalatraVersion = "2.2.1"

  def containerConf = config("container")

  val tomcatDeployKey = TaskKey[Unit]("tomcat-deploy", "deploys the banana stand web war to tomcat")

  val tomcatDeployTask = (tomcatDeployKey in Compile) <<= (Keys.`package` in(Compile)) map {
    (artifact) => {
      val tomcatHost = System.getenv("KRA_TOMCAT_HOST")
      val tomcatUser = System.getenv("KRA_TOMCAT_USER")
      val tomcatPassword = System.getenv("KRA_TOMCAT_PWD")
      println("tomcatHost=" + tomcatHost + " tomcatUser=" + tomcatUser + " tomcatPassword=" + tomcatPassword)
      if(tomcatHost == null || tomcatUser == null || tomcatPassword == null) {
        println("You need to properly set the environment variables: KRA_TOMCAT_HOST, KRA_TOMCAT_USER, " +
          "and KRA_TOMCAT_PWD. The app has not been deployed.")
      } else {
        List("sh", "-c", "curl -u " + tomcatUser + ":" + tomcatPassword + " --upload-file " + artifact +
          " \"http://" + tomcatHost + ":8080/manager/text/deploy?path=/&update=true\"") !;
      }
      None
    }
  }

  lazy val project = Project (
    "karting-application",
    file("."),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(tomcatDeployTask) ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      unmanagedResourceDirectories in Runtime += file("output"),
      fullClasspath in containerConf += file("output"),
        libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatest"  %% "scalatest" % "1.9.1" % "test",
        "org.apache.commons" % "commons-io" % "1.3.2",
        "org.json4s" %% "json4s-jackson" % "3.2.4",
        "ch.qos.logback" % "logback-classic"     % "1.0.13",
        "org.jsoup" % "jsoup" % "1.7.2",
        "joda-time" % "joda-time" % "2.2",
        "org.joda"       % "joda-convert"        % "1.2",
        "org.clapper"    % "grizzled-slf4j_2.10" % "1.0.1",
        "javax.mail" % "mail" % "1.4.1",
        "org.apache.commons" % "commons-io" % "1.3.2",
        "org.scala-lang" % "scala-reflect" % "2.10.0",
        "com.github.tototoshi" %% "scala-csv" % "1.0.0-SNAPSHOT",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
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
