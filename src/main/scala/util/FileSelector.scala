package com.pokutuna.lifelog.util
import java.io.File
import scala.collection.JavaConverters._
import scala.util.matching.Regex

object FileSelector {

  implicit def stringToFile(string: String): File = new File(string)

  def select(rootPath: File, pattern: Regex = ".*".r): Seq[File] = {
    require(rootPath.isDirectory, "FileSelector requires directory as rootPath")

    def expandThis(file: File): Seq[File] = {
      if(file.isDirectory){
        file.listFiles.flatMap(expandThis _)
      } else {
        if(pattern.findFirstIn(file.getName) != None) List(file) else List()
      }
    }
    expandThis(rootPath)
  }
}

object FileSelectorForJava {
  def select(rootPath: File): java.util.List[File] = {
    FileSelector.select(rootPath).asJava
  }

  def select(rootPath: File, pattern: String): java.util.List[File] = {
    FileSelector.select(rootPath, pattern.r).asJava
  }
}
