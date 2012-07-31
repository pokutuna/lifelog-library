import AssemblyKeys._

assemblySettings

name := "lifelog-library"

version := "0.4.0"

organization := "com.pokutuna"

scalaVersion := "2.9.2"

resolvers += "typesace" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
  "play" %% "anorm" % "2.1-0627-sbt12",
  "org.xerial" % "sqlite-jdbc" % "3.7.2"
)

javacOptions ++= Seq("-encoding", "utf8")

scalacOptions += "-deprecation"

watchSources ~= { (files: Seq[File]) =>
  files.filterNot(_.getPath.contains("src/test/resources"))
}

fork in run := true

// javaOptions in run += "-agentlib:hprof=cpu=samples"

traceLevel := 20
