package com.pokutuna.lifelog.util

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata._
import com.drew.metadata.exif._
import java.io.File
import java.util.Date
import scala.collection.JavaConversions._

object ExifExtractor {
  def extract(file: File) = {
    new Exif(ImageMetadataReader.readMetadata(file))
  }

  val reg = """(.+)°(.+)'(.+)\"""".r
  def formatGlobalPosition(str: String):Option[Double] = {
    str match {
      case reg(degs,mins,secs)
      => Some(degs.toDouble + mins.toDouble / 60 + secs.toDouble / 3600)
      case _ => None
    }
  }
}

class Exif(metadata:Metadata) {
  lazy val date:Option[Date] = try {
    val d = metadata.getDirectory(classOf[ExifSubIFDDirectory]).getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
    Some(d)
  } catch {
    case _ => None
  }

  lazy val latitude:Option[Double] = try {
      ExifExtractor.formatGlobalPosition(metadata.getDirectory(classOf[GpsDirectory]).getDescription(GpsDirectory.TAG_GPS_LATITUDE))
    } catch {
      case _ => None
    }

  lazy val longitude:Option[Double] = try {
    ExifExtractor.formatGlobalPosition(metadata.getDirectory(classOf[GpsDirectory]).getDescription(GpsDirectory.TAG_GPS_LONGITUDE))
  } catch {
    case _ => None
  }
}

