package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.`type`.TLangBool
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecIfTest extends AnyFunSuite {

  test("If with true boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true)))))
    val statement = HelperIf(HelperConditionBlock(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))), ifTrue = Some(HelperContent(Some(List(HelperCallObject(List(HelperCallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(res.get.head.asInstanceOf[TLangBool].getValue)
  }

  test("If with false boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false)))))
    val statement = HelperIf(HelperConditionBlock(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))), ifFalse = Some(HelperContent(Some(List(HelperCallObject(List(HelperCallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(!res.get.head.asInstanceOf[TLangBool].getValue)
  }

}
