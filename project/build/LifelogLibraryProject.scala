import sbt._

class LifelogLibraryProject(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++ compileOptions("-encoding", "utf8")
  override def watchPaths = mainSources +++ testSources +++ mainResources

  val scalaTest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
  val scalaQuery = "org.scalaquery" % "scalaquery_2.9.0" % "0.9.4"
  val sqliteJDBC = "org.sqlite" % "sqlitejdbc" % "v056" from "http://files.zentus.com/sqlitejdbc/sqlitejdbc-v056.jar"
}
