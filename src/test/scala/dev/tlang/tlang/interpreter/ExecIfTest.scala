package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangBool
import io.sorne.tlang.ast.common.call.{CallObject, CallVarObject}
import io.sorne.tlang.ast.common.condition
import io.sorne.tlang.ast.common.condition.{Condition, ConditionBlock}
import io.sorne.tlang.ast.common.value.{TLangBool, TLangString}
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecIfTest extends AnyFunSuite {

  test("If with true boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true)))))
    val statement = HelperIf(ConditionBlock(Right(condition.Condition(CallObject(List(CallVarObject("var1")))))), ifTrue = Some(HelperContent(Some(List(CallObject(List(CallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(res.get.head.asInstanceOf[TLangBool].getValue)
  }

  test("If with false boolean first statement") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false)))))
    val statement = HelperIf(ConditionBlock(Right(Condition(CallObject(List(CallVarObject("var1")))))), ifFalse = Some(HelperContent(Some(List(CallObject(List(CallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).toOption.get
    assert(res.isDefined)
    assert(!res.get.head.asInstanceOf[TLangBool].getValue)
  }

  test("If wrong type") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("Anything")))))
    val statement = HelperIf(ConditionBlock(Right(condition.Condition(CallObject(List(CallVarObject("var1")))))), ifFalse = Some(HelperContent(Some(List(CallObject(List(CallVarObject("var1"))))))))
    val res = ExecIf.run(statement, context).left.toOption.get
    assert("WrongType" == res.code)
  }

}
