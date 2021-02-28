name := "SCRAPI"

version := "0.1"

scalaVersion := "2.11.0"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.3"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"