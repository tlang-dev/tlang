package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperCallObject
import org.scalatest.funsuite.AnyFunSuite

class ExecCallObjectTest extends AnyFunSuite {

  test("Get simple variable") {
    val context = Context()
    val statement = HelperCallObject()
    val rest = ExecCallObject.run(statement, context)
  }

}
