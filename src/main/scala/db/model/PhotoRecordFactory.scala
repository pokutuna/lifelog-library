package com.pokutuna.lifelog.db.util

import com.pokutuna.lifelog.db.model.LifelogModel.PhotoRecord
import com.pokutuna.lifelog.util.DateTime
import com.pokutuna.lifelog.util.ExifExtractor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object PhotoRecordFactory {

  def apply(file: File): PhotoRecord = apply(file.getParent, file)

  def apply(directory: String, file: File): PhotoRecord = {
    val image = ImageIO.read(file)
    val exif = ExifExtractor.extract(file)
    val date: Option[DateTime] = exif.date match {
      case Some(d) => Some(DateTime(d))
      case None => None
    }

    PhotoRecord(
      directory,
      file.getName(),
      date.map(_.asString).getOrElse(""),
      exif.latitude.getOrElse(0.0),
      exif.longitude.getOrElse(0.0),
      image.getWidth(),
      image.getHeight(),
      file.length().toInt,
      date.map(_.year).getOrElse(0),
      date.map(_.month).getOrElse(0),
      date.map(_.day).getOrElse(0),
      date.map(_.hour).getOrElse(0),
      date.map(_.minute).getOrElse(0),
      date.map(_.second).getOrElse(0))
  }
}
