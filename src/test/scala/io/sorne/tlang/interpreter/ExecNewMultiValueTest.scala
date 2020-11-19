package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperNewMultiValue
import io.sorne.tlang.ast.helper.call.{HelperCallObject, HelperCallVarObject}
import io.sorne.tlang.interpreter.`type`.TLangString
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecNewMultiValueTest extends AnyFunSuite {

  test("Multiple values with values and called objects") {
    val varToCall = HelperCallObject(List(HelperCallVarObject("var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("myValue2")))))
    val statement = HelperNewMultiValue(List(
      Right(new TLangString("myValue1")),
      Left(varToCall),
      Right(new TLangString("myValue3"))))
    val res = ExecNewMultiValue.run(statement, context).toOption.get.get
    assert("myValue1" == res.head.asInstanceOf[TLangString].getValue)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getValue)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getValue)
  }

}
