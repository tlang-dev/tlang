package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.`type`.Bool
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecIfTest extends AnyFunSuite {

  test("If with true boolean first statement") {
    val context = Context(variables = mutable.Map("var1" -> new Bool(true)))
    val statement = HelperIf(HelperConditionBlock(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))), ifTrue = Some(HelperBlock(Some(List(HelperCallObject(List(HelperCallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(res.get.asInstanceOf[Bool].getValue)
  }

  test("If with false boolean first statement") {
    val context = Context(variables = mutable.Map("var1" -> new Bool(false)))
    val statement = HelperIf(HelperConditionBlock(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))), ifFalse = Some(HelperBlock(Some(List(HelperCallObject(List(HelperCallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(!res.get.asInstanceOf[Bool].getValue)
  }

}
