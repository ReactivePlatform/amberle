name := "amberle"

version := "1.0.0-SNAPSHOT"

organization := "xyz.reactiveplatform.amberle"

scalaVersion := "2.12.8"

lazy val Amberle = (project in file("."))
  .aggregate(docs)

lazy val docs = project.aggregate(`amberle-core`)

lazy val `amberle-core` = project
  .settings(
    crossPaths := false,
    autoScalaLibrary := false,
    startYear := Some(2018),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
  )
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(AutomateJavaFormatterPlugin)

