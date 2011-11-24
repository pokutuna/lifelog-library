package com.pokutuna.lifelog.app

import com.pokutuna.lifelog.db.dao._
import com.pokutuna.lifelog.db.model._
import scalax.file._

object CreateSensingDB {

  def main(args: Array[String]) = {
    println(args.head)
    Path(args.head).matcher("**/*.jpg")
  }
}
