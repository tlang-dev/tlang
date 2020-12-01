package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.interpreter.context.Context
import org.scalatest.funsuite.AnyFunSuite

class ExecPrimitiveValueTest extends AnyFunSuite {

  test("Simple new value") {
    val statement = new TLangString("myValue")
    val res = ExecPrimitiveValue.run(statement, Context())
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getValue)
  }

}
