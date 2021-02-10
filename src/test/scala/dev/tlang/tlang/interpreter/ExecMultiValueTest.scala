package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{MultiValue, TLangString}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecMultiValueTest extends AnyFunSuite {

  test("Multiple values with values and called objects") {
    val varToCall = CallObject(List(CallVarObject("var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("myValue2")))))
    val statement = MultiValue(List(
      new TLangString("myValue1"),
      varToCall,
      new TLangString("myValue3")))
    val res = ExecMultiValue.run(statement, context).toOption.get.get
    assert("myValue1" == res.head.asInstanceOf[TLangString].getValue)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getValue)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getValue)
  }

}
