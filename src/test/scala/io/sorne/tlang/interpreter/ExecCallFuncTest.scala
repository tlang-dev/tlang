package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.model.`new`.{ModelNewAttribute, ModelNewPrimitiveValue}
import io.sorne.tlang.interpreter.`type`.Bool
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

  test("Run function with one simple parameter") {
    val caller = HelperCallObject(List(HelperCallVarObject("var1")))
    val block = HelperBlock(Some(List(caller)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(ModelNewAttribute(Some("valToReturn"), ModelNewPrimitiveValue(value = "String")))))), block = block)
    val funcCaller = HelperCallFuncObject("myFunc", Some(List(HelperCallFuncParam(List()))))
    val context = Context(variables = mutable.Map("var1" -> new Bool(true)), functions = mutable.Map("myFunc" -> funcDef))
    val statement = HelperCallObject(List(funcCaller))
    val res = ExecCallFunc.run(statement, context).toOption.get.get
    assert(res.asInstanceOf[Bool].getValue)
  }

}
