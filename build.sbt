name := "DatabaseProject"

version := "1.0"

scalaVersion := "2.11.8"

enablePlugins(JavaAppPackaging)

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
libraryDependencies += "org.scala-lang" % "scala-swing" % "2.11.0-M7"

    