package com.pokutuna.lifelog.test
import com.pokutuna.lifelog.db.dao._

class DatabaseSpec extends SpecHelper {
  describe("Database") {
    it("should have database url") {
      val db = new Database("hoge")
      db.databaseUrl should be ("jdbc:sqlite:hoge")
    }

    it("should execute query") {
      val db = new Database(":memory:")
      db.withConnection { c =>
        val stat = c.createStatement()
        stat.executeUpdate("create table hoge(num integer);")
        stat.executeUpdate("insert into hoge values (10);")
        val resultSet = stat.executeQuery("select * from hoge;")
        resultSet.next()
        resultSet.getInt("num") should be (10)
      }
    }
  }
}
