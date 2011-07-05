package com.pokutuna.LifelogDBC.model

object LifelogModel {
  case class PhotoRecord(
    directory: String,
    filename: String,
    orgDate: String,
    latitude: Double,
    longitude: Double,
    width: Int,
    height: Int,
    fileSize: Int,
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int
  )
}
