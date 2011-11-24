package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import com.pokutuna.lifelog.util._
import java.io.File

object CreateLifelogDB {

  val defaultDbFile = "lifelog.db"
  val imageFilenamePattern = """\.jpg$|\.JPG$|\.png$|\.PNG$""".r

  def run(rootPath: String, dbPath: Option[String]) = {
    FileSelector.select(new File(rootPath), imageFilenamePattern).foreach(println)
  }

  def main(args: Array[String]) = {
    val rootPath = args.headOption match {
      case Some(path) => path
      case None       => throw new RuntimeException("require rootPath for importing photos")
    }

    val dbPath = args.tail.headOption
    run(rootPath, dbPath)
  }
}
