package com.pokutuna.lifelog.util

import java.io._

object MD5Digest {

  def apply(file: File) = md5sum(file)

  def md5sum(file: File): String = {
    val stream = new FileInputStream(file)
    try {
      val buf = Stream.continually( stream.read ).takeWhile( -1 != _ ).map{ _.byteValue }.toArray
      val md5 = java.security.MessageDigest.getInstance("MD5")
      md5.update(buf)
      return md5.digest().map("%02x".format(_)).mkString
    } finally {
      stream.close()
    }
  }
}
