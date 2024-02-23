package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{LazyValue, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.ast._
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.core.Null
import tlang.internal.TmplStringID

import scala.collection.mutable

class ExecCallFuncTest extends AnyFunSuite {

 /* test("Run function with one simple parameter") {
    val callInsideFunc = CallObject(Null.empty, List(CallVarObject(Null.empty, "valToReturn")))
    val block = HelperContent(Null.empty, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(Null.empty, None, new String("String"))))))), None, block = block)
    val caller = SetAttribute(Null.empty, value = Operation(Null.empty, None, Right(CallObject(Null.empty, List(CallVarObject(Null.empty, "var1"))))))
    val funcCaller = CallFuncObject(Null.empty, Some("myFunc"), Some(List(CallFuncParam(Null.empty, Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(Null.empty, "MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallFunc.run(funcCaller, context).toOption.get.get
    assert("MyValue" == res.head.asInstanceOf[TLangString].getElement)
  }

  test("Call template without parameters") {
    val block = LangBlock(Null.empty, "myBlock", List("scala"), None, LangFullBlock(Null.empty, Some(LangPkg(Null.empty, List(new TmplStringID(Null.empty, new core.String("myPackage")))))))
    val tmplCaller = CallFuncObject(Null.empty, Some("myTmpl"), None)
    val context = Context(List(Scope(templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[LangBlockAsValue]
    assert("myPackage" == res.block.asInstanceOf[LangBlock].content.pkg.get.parts.head.asInstanceOf[TmplStringID].getId.toString)
  }

  test("Call template with parameters") {
    val block = LangBlock(None, "myBlock", List("scala"), Some(List(NativeType(None, HelperParam(None, Some("var1"), ObjType(Null.empty, None, "String"))))), LangFullBlock(None, Some(LangPkg(None, List(TmplStringID(None, "myPackage"))))))
    val caller = SetAttribute(Null.empty, value = Operation(Null.empty, None, Right(CallObject(Null.empty, List(CallVarObject(Null.empty, "var1"))))))
    val tmplCaller = CallFuncObject(Null.empty, Some("myTmpl"), Some(List(CallFuncParam(Null.empty, Some(List(caller))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(Null.empty, "MyValue")), templates = mutable.Map("myTmpl" -> block))))
    val res = ExecCallFunc.run(tmplCaller, context).toOption.get.get.head.asInstanceOf[LangBlockAsValue]
    assert("MyValue" == res.context.scopes.head.variables.head._2.asInstanceOf[TLangString].getElement)
    assert("myPackage" == res.block.asInstanceOf[LangBlock].content.pkg.get.parts.head.asInstanceOf[TmplStringID].getId.toString)
  }

  test("Merge callers") {
    val refFuncCaller = CallRefFuncObject(None, None, Some(List(
      CallFuncParam(None, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(LazyValue(Null.empty, None, Some(TLangString))))), SetAttribute(None, None, Operation(None, None, Right(new TLangString(None, "c1p2")))), SetAttribute(None, None, Operation(None, None, Right(new TLangString(Null.empty, "c1p3"))))))),
      CallFuncParam(None, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(new TLangString(Null.empty, "c2p1")))), SetAttribute(None, None, Operation(None, None, Right(LazyValue(None, None, Some(TLangString))))), SetAttribute(None, None, Operation(None, None, Right(new TLangString(Null.empty, "c2p3"))))))),
      CallFuncParam(None, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(new TLangString(Null.empty, "c3p1")))), SetAttribute(None, None, Operation(None, None, Right(new TLangString(None, "c3p2")))), SetAttribute(None, None, Operation(None, None, Right(LazyValue(Null.empty, None, Some(TLangString)))))))),
    )))

    val funcCaller = CallFuncObject(Null.empty, None, Some(List(
      CallFuncParam(Null.empty, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(new TLangString(Null.empty, "c1p1"))))))),
      CallFuncParam(Null.empty, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(new TLangString(Null.empty, "c2p2"))))))),
      CallFuncParam(Null.empty, Some(List(SetAttribute(Null.empty, None, Operation(Null.empty, None, Right(new TLangString(Null.empty, "c3p3"))))))),
    )))

    val res = ExecCallFunc.mergeCallers(funcCaller, refFuncCaller).currying.get
    assert("c1p1" == res.head.params.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c1p2" == res.head.params.get(1).value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c1p3" == res.head.params.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c2p1" == res(1).params.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c2p2" == res(1).params.get(1).value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c2p3" == res(1).params.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c3p1" == res.last.params.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c3p2" == res.last.params.get(1).value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("c3p3" == res.last.params.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
  }*/

}
