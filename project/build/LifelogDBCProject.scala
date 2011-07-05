import sbt._

class LifelogDBCProject(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++
  compileOptions("-encoding", "utf8")

  val scalaTest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
  val scalaQuery = "org.scalaquery" % "scalaquery_2.9.0" % "0.9.4"
}
