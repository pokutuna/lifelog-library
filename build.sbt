name := "lifelog-library"

version := "0.2.1"

organization := "com.pokutuna"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
  "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5",
  "org.sqlite" % "sqlitejdbc" % "v056" from "http://files.zentus.com/sqlitejdbc/sqlitejdbc-v056.jar"
)

javacOptions ++= Seq("-encoding", "utf8")

scalacOptions += "-deprecation"

watchSources ~= { (files: Seq[File]) =>
  files.filterNot(_.getPath.contains("src/test/resources"))
}

fork in run := true

javaOptions in run += "-agentlib:hprof"

