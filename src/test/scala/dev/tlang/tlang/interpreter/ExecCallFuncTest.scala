package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.tmpl.{TmplBlockAsValue, TmplStringID}
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

  test("Run function with one simple parameter") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val funcCaller = CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallFunc.run(funcCaller, context).toOption.get.get
    assert("MyValue"==res.head.asInstanceOf[TLangString].getValue)
  }

  test("Call template without parameters") {
    val block = TmplBlock("myBlock", "scala", None, Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val tmplCaller = CallFuncObject(Some("myTmpl"), None)
    val context = Context(List(Scope(templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
  }

  test("Call template with parameters") {
    val block = TmplBlock("myBlock", "scala", Some(List(HelperParam(Some("var1"), HelperObjType("String")))), Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val tmplCaller = CallFuncObject(Some("myTmpl"), Some(List(CallFuncParam(Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("MyValue" == res.context.scopes.head.variables.head._2.asInstanceOf[TLangString].getValue)
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
  }

}
