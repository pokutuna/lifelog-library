package com.pokutuna.lifelog.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Spec}
import org.scalatest.matchers.ShouldMatchers

trait SpecHelper extends Spec with ShouldMatchers
  with BeforeAndAfterAll with BeforeAndAfterEach
