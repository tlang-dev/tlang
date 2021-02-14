package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncParam, CallRefFuncObject, SetAttribute}
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.Context
import org.scalatest.funsuite.AnyFunSuite

class ExecCallRefFuncTest extends AnyFunSuite {

  test("Call func") {
    var res = ""

    val calledFunc = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("param1"), HelperObjType(TLangString.getType)))))), None, HelperContent(Some(List(
      HelperInternalFunc((context: Context) => {
        res += context.scopes.head.variables("param1").asInstanceOf[TLangString].getValue
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(Some(List(CallFuncParam(Some(List(SetAttribute(value = new TLangString("myValue"))))))), Some(Left(calledFunc)))

    ExecCallRefFunc.run(call, Context())
    assert("myValue" == res)
  }

  test("Call tmpl") {
    val calledTmpl = TmplBlock("myTmpl", "scala", Some(List(HelperParam(Some("param1"), HelperObjType(TLangString.getType)))), Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val call = CallRefFuncObject(Some(List(CallFuncParam(Some(List(SetAttribute(value = new TLangString("myValue"))))))), Some(Right(calledTmpl)))

    val res = ExecCallRefFunc.run(call, Context()).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
    assert("myValue" == res.context.scopes.head.variables.head._2.asInstanceOf[TLangString].getValue)
  }

}
