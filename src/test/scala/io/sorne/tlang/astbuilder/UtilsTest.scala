package io.sorne.tlang.astbuilder

import org.scalatest.funsuite.AnyFunSuite

class UtilsTest extends AnyFunSuite {

  test("Extract String") {
    assert("".equals(Utils.extraString("")))
    assert("".equals(Utils.extraString("\"\"")))
    assert("test".equals(Utils.extraString("\"test\"")))
    assert("test".equals(Utils.extraString("test")))
  }
}
