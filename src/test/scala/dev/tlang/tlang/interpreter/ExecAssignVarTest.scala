package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{MultiValue, TLangString}
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{AssignVar, MultiValue, TLangString}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecAssignVarTest extends AnyFunSuite {

  test("Assign to var") {
    val statement = AssignVar("myVar", None, new TLangString("myValue"))
    val scope = Scope()
    val res = ExecAssignVar.run(statement, Context(List(scope)))
    assert("myValue" == scope.variables("myVar").asInstanceOf[TLangString].getValue)
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getValue)
  }

  test("Assign to var multiple values") {
    val statement = AssignVar("myVar", None, MultiValue(List(
      new TLangString("myValue1"),
      new TLangString("myValue2"),
      new TLangString("myValue3"))))
    val scope = Scope()
    val res = ExecAssignVar.run(statement, Context(List(scope))).toOption.get.get
    assert("myValue1" == scope.variables("myVar").asInstanceOf[MultiValue].values.head.asInstanceOf[TLangString].getValue)
    assert("myValue2" == scope.variables("myVar").asInstanceOf[MultiValue].values(1).asInstanceOf[TLangString].getValue)
    assert("myValue3" == scope.variables("myVar").asInstanceOf[MultiValue].values.last.asInstanceOf[TLangString].getValue)

    assert("myValue1" == res.head.asInstanceOf[TLangString].getValue)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getValue)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getValue)
  }

  test("Assign value from called object") {
    val varToCall = CallObject(List(CallVarObject("var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("myValue")))))
    val statement = AssignVar("myVar", None, varToCall)
    val res = ExecAssignVar.run(statement, context)
    assert("myValue" == context.scopes.head.variables("myVar").asInstanceOf[TLangString].getValue)
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getValue)
  }

  test("Assign multiple values with HelperNewMultiValue") {
    val varToCall = CallObject(List(CallVarObject("var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("myValue2")))))
    val values = MultiValue(List(
      new TLangString("myValue1"),
      varToCall,
      new TLangString("myValue3")))
    val statement = AssignVar("myVar", None, values)
    val res = ExecAssignVar.run(statement, context).toOption.get.get
    assert("myValue1" == res.head.asInstanceOf[TLangString].getValue)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getValue)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getValue)
  }

}
