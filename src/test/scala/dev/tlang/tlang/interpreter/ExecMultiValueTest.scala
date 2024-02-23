package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{MultiValue, TLangString}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecMultiValueTest extends AnyFunSuite {

  /*test("Multiple values with values and called objects") {
    val varToCall = CallObject(None, List(CallVarObject(None, "var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "myValue2")))))
    val statement = MultiValue(None, List(
      new TLangString(None, "myValue1"),
      varToCall,
      new TLangString(None, "myValue3")))
    val res = ExecMultiValue.run(statement, context).toOption.get.get
    assert("myValue1" == res.head.asInstanceOf[TLangString].getElement)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getElement)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getElement)
  }*/

}
