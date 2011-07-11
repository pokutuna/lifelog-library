package com.pokutuna.lifelog.test

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach

trait SpecHelper extends Spec with ShouldMatchers
  with BeforeAndAfterAll with BeforeAndAfterEach
