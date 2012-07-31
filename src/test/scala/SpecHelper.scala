package com.pokutuna.lifelog.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec}
import org.scalatest.matchers.ShouldMatchers

trait SpecHelper extends FunSpec with ShouldMatchers
  with BeforeAndAfterAll with BeforeAndAfterEach
