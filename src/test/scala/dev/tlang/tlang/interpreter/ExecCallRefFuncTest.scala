package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.{CallFuncParam, CallRefFuncObject, SetAttribute}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.Context
import org.scalatest.funsuite.AnyFunSuite

class ExecCallRefFuncTest extends AnyFunSuite {

  test("Call func") {
    var res = ""

    val calledFunc = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("param1"), ObjType(None, None, TLangString.getType)))))), None, HelperContent(None, Some(List(
      HelperInternalFunc((context: Context) => {
        res += context.scopes.head.variables("param1").asInstanceOf[TLangString].getElement
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(None, None, Some(List(CallFuncParam(None, Some(List(SetAttribute(None, value = Operation(None, None, Right(new TLangString(None, "myValue"))))))))), Some(Left(calledFunc)))

    ExecCallRefFunc.run(call, Context())
    assert("myValue" == res)
  }

  test("Call tmpl") {
    val calledTmpl = TmplBlock(None, "myTmpl", "scala", Some(List(HelperParam(None, Some("param1"), ObjType(None, None, TLangString.getType)))), Some(new TmplPkg(List(TmplStringID(None, "myPackage")))))
    val call = CallRefFuncObject(None, None, Some(List(CallFuncParam(None, Some(List(SetAttribute(None, value = Operation(None, None, Right(new TLangString(None, "myValue"))))))))), Some(Right(calledTmpl)))

    val res = ExecCallRefFunc.run(call, Context()).toOption.get.get.head.asInstanceOf[TmplBlockAsValue]
    assert("myPackage" == res.block.pkg.get.parts.head.asInstanceOf[TmplStringID].id)
    assert("myValue" == res.context.scopes.head.variables.head._2.asInstanceOf[TLangString].getElement)
  }

}
