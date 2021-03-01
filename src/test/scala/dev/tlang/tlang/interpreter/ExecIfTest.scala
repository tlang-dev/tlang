package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.condition
import dev.tlang.tlang.ast.common.condition.{Condition, ConditionBlock}
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecIfTest extends AnyFunSuite {

  test("If with true boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true)))))
    val statement = HelperIf(None, ConditionBlock(None, Right(condition.Condition(None, CallObject(None, List(CallVarObject(None, "var1")))))), ifTrue = Some(HelperContent(None, Some(List(CallObject(None, List(CallVarObject(None, "var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(res.get.head.asInstanceOf[TLangBool].getValue)
  }

  test("If with false boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false)))))
    val statement = HelperIf(None, ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1")))))), ifFalse = Some(HelperContent(None, Some(List(CallObject(None, List(CallVarObject(None, "var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(!res.get.head.asInstanceOf[TLangBool].getValue)
  }

  test("If wrong type") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "Anything")))))
    val statement = HelperIf(None, ConditionBlock(None, Right(condition.Condition(None, CallObject(None, List(CallVarObject(None, "var1")))))), ifFalse = Some(HelperContent(None, Some(List(CallObject(None, List(CallVarObject(None, "var1"))))))))
    val res = ExecIf.run(statement, context).left.toOption.get
    assert("WrongType" == res.code)
  }

}
