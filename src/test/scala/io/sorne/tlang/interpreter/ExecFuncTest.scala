package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.helper.call.{HelperCallObject, HelperCallVarObject}
import io.sorne.tlang.interpreter.`type`.TLangBool
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecFuncTest extends AnyFunSuite {

  test("Run simple function") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true)))))
    val caller = HelperCallObject(List(HelperCallVarObject("var1")))
    val block = HelperContent(Some(List(caller)))
    val statement = HelperFunc("myFunc", block = block)
    val res = ExecFunc.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[TLangBool].getValue)
  }

}
