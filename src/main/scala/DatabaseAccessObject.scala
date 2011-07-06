package com.pokutuna.Lifelog.db.dao

import org.scalaquery.ql.basic.AbstractBasicTable
import org.scalaquery.ql.basic.BasicProfile
import org.scalaquery.ql.basic.BasicTable
import org.scalaquery.ql.extended.ExtendedProfile
import org.scalaquery.ql.extended.ExtendedTable
import org.scalaquery.session._
import org.scalaquery.ql._

abstract class DatabaseAccessObject(path:String, driver:ExtendedProfile) {
  import driver.Implicit._

  lazy val db: Database = Database.forURL(path, driver = "org.sqlite.JDBC")

  val tables: List[ExtendedTable[_]]

  def createAll = {
    db.withSession{ session:Session =>
      tables.foreach{ t =>
        t.ddl.create(session)
      }
    }
  }

  def dropAll = {
    db.withSession{ session:Session =>
      tables.foreach{ t =>
        t.ddl.drop(session)
      }
    }
  }
}
