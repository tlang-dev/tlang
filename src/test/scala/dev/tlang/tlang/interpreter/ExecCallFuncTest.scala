package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.{LazyValue, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

  test("Run function with one simple parameter") {
    val callInsideFunc = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), HelperObjType(None, "String")))))), None, block = block)
    val caller = SetAttribute(None, value = CallObject(None, List(CallVarObject(None, "var1"))))
    val funcCaller = CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallFunc.run(funcCaller, context).toOption.get.get
    assert("MyValue" == res.head.asInstanceOf[TLangString].getValue)
  }

  test("Call template without parameters") {
    val block = TmplBlock(None, "myBlock", "scala", None, Some(new TmplPkg(List(TmplStringID(None, "myPackage")))))
    val tmplCaller = CallFuncObject(None, Some("myTmpl"), None)
    val context = Context(List(Scope(templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
  }

  test("Call template with parameters") {
    val block = TmplBlock(None, "myBlock", "scala", Some(List(HelperParam(None, Some("var1"), HelperObjType(None, "String")))), Some(new TmplPkg(List(TmplStringID(None, "myPackage")))))
    val caller = SetAttribute(None, value = CallObject(None, List(CallVarObject(None, "var1"))))
    val tmplCaller = CallFuncObject(None, Some("myTmpl"), Some(List(CallFuncParam(None, Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue")), templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("MyValue" == res.context.scopes.head.variables.head._2.asInstanceOf[TLangString].getValue)
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
  }

  test("Merge callers") {
    val refFuncCaller = CallRefFuncObject(None, None, Some(List(
      CallFuncParam(None, Some(List(SetAttribute(None, None, LazyValue(None, None, Some(TLangString))), SetAttribute(None, None, new TLangString(None, "c1p2")), SetAttribute(None, None, new TLangString(None, "c1p3"))))),
      CallFuncParam(None, Some(List(SetAttribute(None, None, new TLangString(None, "c2p1")), SetAttribute(None, None, LazyValue(None, None, Some(TLangString))), SetAttribute(None, None, new TLangString(None, "c2p3"))))),
      CallFuncParam(None, Some(List(SetAttribute(None, None, new TLangString(None, "c3p1")), SetAttribute(None, None, new TLangString(None, "c3p2")), SetAttribute(None, None, LazyValue(None, None, Some(TLangString)))))),
    )))

    val funcCaller = CallFuncObject(None, None, Some(List(
      CallFuncParam(None, Some(List(SetAttribute(None, None, new TLangString(None, "c1p1"))))),
      CallFuncParam(None, Some(List(SetAttribute(None, None, new TLangString(None, "c2p2"))))),
      CallFuncParam(None, Some(List(SetAttribute(None, None, new TLangString(None, "c3p3"))))),
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
