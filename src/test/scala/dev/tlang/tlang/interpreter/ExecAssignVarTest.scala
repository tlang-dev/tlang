package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{AssignVar, MultiValue, TLangString}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecAssignVarTest extends AnyFunSuite {

  test("Assign to var") {
    val statement = AssignVar(None, "myVar", None, new TLangString(None,"myValue"))
    val scope = Scope()
    val res = ExecAssignVar.run(statement, Context(List(scope)))
    assert("myValue" == scope.variables("myVar").asInstanceOf[TLangString].getElement)
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getElement)
  }

  test("Assign to var multiple values") {
    val statement = AssignVar(None,"myVar", None, MultiValue(None,List(
      new TLangString(None,"myValue1"),
      new TLangString(None,"myValue2"),
      new TLangString(None,"myValue3"))))
    val scope = Scope()
    val res = ExecAssignVar.run(statement, Context(List(scope))).toOption.get.get
    assert("myValue1" == scope.variables("myVar").asInstanceOf[MultiValue].values.head.asInstanceOf[TLangString].getElement)
    assert("myValue2" == scope.variables("myVar").asInstanceOf[MultiValue].values(1).asInstanceOf[TLangString].getElement)
    assert("myValue3" == scope.variables("myVar").asInstanceOf[MultiValue].values.last.asInstanceOf[TLangString].getElement)

    assert("myValue1" == res.head.asInstanceOf[TLangString].getElement)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getElement)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getElement)
  }

  test("Assign value from called object") {
    val varToCall = CallObject(None, List(CallVarObject(None, "var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "myValue")))))
    val statement = AssignVar(None, "myVar", None, varToCall)
    val res = ExecAssignVar.run(statement, context)
    assert("myValue" == context.scopes.head.variables("myVar").asInstanceOf[TLangString].getElement)
    assert("myValue" == res.toOption.get.get.head.asInstanceOf[TLangString].getElement)
  }

  test("Assign multiple values with HelperNewMultiValue") {
    val varToCall = CallObject(None, List(CallVarObject(None, "var1")))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "myValue2")))))
    val values = MultiValue(None, List(
      new TLangString(None, "myValue1"),
      varToCall,
      new TLangString(None,"myValue3")))
    val statement = AssignVar(None, "myVar", None, values)
    val res = ExecAssignVar.run(statement, context).toOption.get.get
    assert("myValue1" == res.head.asInstanceOf[TLangString].getElement)
    assert("myValue2" == res(1).asInstanceOf[TLangString].getElement)
    assert("myValue3" == res.last.asInstanceOf[TLangString].getElement)
  }

}
