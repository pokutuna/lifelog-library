package com.pokutuna.lifelog.sketch

object SketchHoge {

  class Hoge(num: Int) {
    implicit val number:Int = num

    object Piyo {
      def doPiyo = { println(number) }
    }
  }

  def main(args: Array[String]) = {
    val a = new Hoge(1)
    a.Piyo.doPiyo
    val b = new Hoge(5)
    b.Piyo.doPiyo
  }
}
