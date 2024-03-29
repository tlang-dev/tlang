package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangBool
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.TLangBool
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecFuncTest extends AnyFunSuite {

  test("Run simple function") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true)))))
    val caller = CallObject(None, List(CallVarObject(None, "var1")))
    val block = HelperContent(None, Some(List(caller)))
    val statement = HelperFunc(None, "myFunc", block = block)
    val res = ExecFunc.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[TLangBool].getElement)
  }

}
