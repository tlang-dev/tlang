package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.model.`new`.{ModelNewAttribute, ModelNewPrimitiveValue}
import io.sorne.tlang.interpreter.`type`.{Bool, String}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

  test("Run function with one simple parameter") {
    val callInsideFunc = HelperCallObject(List(HelperCallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(ModelNewAttribute(Some("valToReturn"), ModelNewPrimitiveValue(value = "String")))))), block = block)
    val caller = HelperCallObject(List(HelperCallVarObject("var1")))
    val funcCaller = HelperCallFuncObject("myFunc", Some(List(HelperCallFuncParam(List(caller)))))
    val context = Context(variables = mutable.Map("var1" ->  new String("MyValue")), functions = mutable.Map("myFunc" -> funcDef))
    val res = ExecCallFunc.run(funcCaller, context).toOption.get.get
    assert("MyValue".equals(res.asInstanceOf[String].getValue))
  }

}
