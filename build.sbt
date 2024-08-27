ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "zio-test"
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-http" % "3.0.0-RC9",
  "dev.zio" %% "zio-kafka" % "2.8.0",
  "dev.zio" %% "zio-json" % "0.6.2",
  "dev.zio" %% "zio-streams" % "2.0.9",
)