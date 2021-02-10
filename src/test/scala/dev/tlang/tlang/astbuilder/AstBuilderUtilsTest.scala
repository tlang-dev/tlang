package dev.tlang.tlang.astbuilder

import org.scalatest.funsuite.AnyFunSuite

class AstBuilderUtilsTest extends AnyFunSuite {

  test("Extract String") {
    assert("" == AstBuilderUtils.extraString(""))
    assert("" == AstBuilderUtils.extraString("\"\""))
    assert("test" == AstBuilderUtils.extraString("\"test\""))
    assert("test" == AstBuilderUtils.extraString("test"))
  }

  test("Extract Text") {
    assert("" == AstBuilderUtils.extraText(""))
    assert("" == AstBuilderUtils.extraText("\"\"\"\"\"\""))
    assert("test" == AstBuilderUtils.extraText("\"\"\"test\"\"\""))
    assert("test" == AstBuilderUtils.extraText("test"))
  }
}
