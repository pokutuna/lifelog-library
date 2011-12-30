package com.pokutuna.lifelog.db.model

import anorm._
import anorm.SqlParser._
import java.sql._

case class Device(id: Pk[Int], address: String, deviceType: String, nomadic: String) {
  def this(address: String, deviceType: String, nomadic: String) = {
    this(NotAssigned, address, deviceType, nomadic)
  }
}
