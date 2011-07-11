package com.pokutuna.lifelog.db.dao

import com.pokutuna.lifelog.db.model.LifelogModel._
import com.pokutuna.lifelog.db.table.LifelogTable._
import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended.{ExtendedTable => Table, _}
import org.scalaquery.session._
import org.scalaquery.session.Database._

class LifelogDAO(path: String) extends DatabaseAccessObject(path, SQLiteDriver) {

  val tables = List(Photos)

  def photoTakenIn(from: String, to: String): Seq[PhotoRecord] = {
    db.withSession {
      val q = for {
        b <- Photos.where(p => p.dateTime >= from && p.dateTime <= to)
        _ <- Query.orderBy(b.dateTime asc)
      } yield b
      q.list
    }
  }

  def photoTakenWhere(latMin: Double, latMax: Double, lonMin: Double, lonMax: Double): Seq[PhotoRecord] = {
    db.withSession {
      Photos.where{ p =>
        p.latitude >= latMin && p.latitude <= latMax &&
        p.longitude >= lonMin && p.longitude <= lonMax }.list
    }
  }

  def photoByName(filename: String): Seq[PhotoRecord] = {
    db.withSession {
      Photos.where(_.filename is filename).list
    }
  }

  def photoByName(dirname:String, filename: String): Seq[PhotoRecord] = {
    db.withSession {
      Photos.where(p => (p.directory is dirname) && (p.filename is filename)).list
    }
  }

  def existsFile(dirname: String, filename: String): Boolean = {
    db.withSession {
      Photos.where { p =>
        (p.directory is dirname) && (p.filename is filename)
      }.firstOption match {
        case Some(_) => true
        case None => false
      }
    }
  }

  def existsFile(filename: String): Boolean = {
    db.withSession {
      Photos.where { p =>
        p.filename is filename
      }.firstOption match {
        case Some(_) => true
        case None => false
      }
    }
  }

  def insertPhotoRecord(photo: PhotoRecord): Unit = {
    db.withSession {
      Photos.forInsert.insert(photo)
    }
  }
}
