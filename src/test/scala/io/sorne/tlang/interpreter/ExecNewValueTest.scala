package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperNewValue
import io.sorne.tlang.ast.model.let.ModelLetMultiValue
import io.sorne.tlang.interpreter.`type`.TLangString
import io.sorne.tlang.interpreter.context.Context
import org.scalatest.funsuite.AnyFunSuite

class ExecNewValueTest extends AnyFunSuite {

  test("Simple new value") {
    val statement = HelperNewValue(new TLangString("myValue"))
    val res = ExecNewValue.run(statement, Context())
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getValue)
  }

}
