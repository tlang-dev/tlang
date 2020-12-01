package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

  test("Run function with one simple parameter") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = CallObject(List(CallVarObject("var1")))
    val funcCaller = CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(List(caller)))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallFunc.run(funcCaller, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call template without parameters") {
    val block = TmplBlock("myBlock", "scala", None, Some("myPackage"))
    val tmplCaller = CallFuncObject(Some("myTmpl"), None)
    val context = Context(List(Scope(templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("myPackage" == res.block.pkg.get)
  }

  test("Call template with parameters") {
    val block = TmplBlock("myBlock", "scala", Some(List("var1")), Some("myPackage"))
    val caller = CallObject(List(CallVarObject("var1")))
    val tmplCaller = CallFuncObject(Some("myTmpl"), Some(List(CallFuncParam(List(caller)))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("MyValue" == res.params.head.asInstanceOf[TLangString].getValue)
    assert("myPackage" == res.block.pkg.get)
  }

}
