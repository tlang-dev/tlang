package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.interpreter.context.Context
import org.scalatest.funsuite.AnyFunSuite

class ExecPrimitiveValueTest extends AnyFunSuite {

  test("Simple new value") {
    val statement = new TLangString(None,"myValue")
    val res = ExecPrimitiveValue.run(statement, Context())
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getElement)
  }

}
