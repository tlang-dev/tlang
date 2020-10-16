package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.`type`.Bool
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecFuncTest extends AnyFunSuite {

  test("Run simple function") {
    val context = Context(variables = mutable.Map("var1" -> new Bool(true)))
    val caller = HelperCallObject(List(HelperCallVarObject("var1")))
    val block = HelperBlock(Some(List(caller)))
    val statement = HelperFunc("myFunc", block = block)
    val res = ExecFunc.run(statement, context).toOption.get.get
    assert(res.asInstanceOf[Bool].getValue)
  }

}