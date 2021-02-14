package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{LazyValue, TLangString}
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
    assert("MyValue" == res.head.asInstanceOf[TLangString].getValue)
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

  test("Merge callers") {
    val refFuncCaller = CallRefFuncObject(Some(List(
      CallFuncParam(Some(List(SetAttribute(None, LazyValue(None, TLangString)), SetAttribute(None, new TLangString("c1p2")), SetAttribute(None, new TLangString("c1p3"))))),
      CallFuncParam(Some(List(SetAttribute(None, new TLangString("c2p1")), SetAttribute(None, LazyValue(None, TLangString)), SetAttribute(None, new TLangString("c2p3"))))),
      CallFuncParam(Some(List(SetAttribute(None, new TLangString("c3p1")), SetAttribute(None, new TLangString("c3p2")), SetAttribute(None, LazyValue(None, TLangString))))),
    )))

    val funcCaller = CallFuncObject(None, Some(List(
      CallFuncParam(Some(List(SetAttribute(None, new TLangString(("c1p1")))))),
      CallFuncParam(Some(List(SetAttribute(None, new TLangString(("c2p2")))))),
      CallFuncParam(Some(List(SetAttribute(None, new TLangString(("c3p3")))))),
    )))

    val res = ExecCallFunc.mergeCallers(funcCaller, refFuncCaller).currying.get
    assert("c1p1" == res.head.params.get.head.value.asInstanceOf[TLangString].getValue)
    assert("c1p2" == res.head.params.get(1).value.asInstanceOf[TLangString].getValue)
    assert("c1p3" == res.head.params.get.last.value.asInstanceOf[TLangString].getValue)
    assert("c2p1" == res(1).params.get.head.value.asInstanceOf[TLangString].getValue)
    assert("c2p2" == res(1).params.get(1).value.asInstanceOf[TLangString].getValue)
    assert("c2p3" == res(1).params.get.last.value.asInstanceOf[TLangString].getValue)
    assert("c3p1" == res.last.params.get.head.value.asInstanceOf[TLangString].getValue)
    assert("c3p2" == res.last.params.get(1).value.asInstanceOf[TLangString].getValue)
    assert("c3p3" == res.last.params.get.last.value.asInstanceOf[TLangString].getValue)
  }

}
