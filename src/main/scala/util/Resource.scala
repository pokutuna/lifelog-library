package com.pokutuna.lifelog.util

import java.net.URL

object Resource {
  def getUrl(path: String): URL = {
    val url = getClass.getResource(path)
    if (url == null) throw new RuntimeException("no resource")
    return url
  }
}
