package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperCallObject, HelperIf}
import org.scalatest.funsuite.AnyFunSuite

class ExecIfTest extends AnyFunSuite {

  test("If with boolean first statement") {
    val statement = HelperIf()
    val res = ExecIf.run()
  }

}
