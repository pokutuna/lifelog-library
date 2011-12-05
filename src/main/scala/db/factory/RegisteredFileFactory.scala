package com.pokutuna.lifelog.db.factory

import com.pokutuna.lifelog.db.model.RegisteredFile
import com.pokutuna.lifelog.util.MD5Digest
import java.io.File

object RegisteredFileFactory {

  def apply(file: File): RegisteredFile = {
    new RegisteredFile(file.getName, MD5Digest(file))
  }

}
