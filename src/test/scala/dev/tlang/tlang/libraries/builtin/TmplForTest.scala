package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.{ArrayValue, LazyValue, SimpleAttribute, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.ExecFunc
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class TmplForTest extends AnyFunSuite {

  test("Simple for") {
    var res = ""

    val array = ArrayValue(Some(List(SimpleAttribute(value = new TLangString("val1")),
      SimpleAttribute(value = new TLangString("val2")),
      SimpleAttribute(value = new TLangString("val3")))))

    val calledFunc = HelperFunc("anyFunc", Some(List(HelperCurrying(List(HelperParam(Some("param1"), HelperObjType(TLangString.getType)))))), None, HelperContent(Some(List(
      HelperInternalFunc((context: Context) => {
        //        res += context.scopes.last.variables("param1").asInstanceOf[TLangString].getValue
        res += ContextUtils.findVar(context, "param1").get.asInstanceOf[TLangString].getValue
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(None, Some(List(CallFuncParam(Some(List(SetAttribute(value = LazyValue(None, Some(TLangString)))))))), Some(Left(calledFunc)))

    val context = Context(List(Scope(
      variables = mutable.Map("array" -> array),
      refFunctions = mutable.Map("refFunc" -> call)
    )))

    ExecFunc.run(TmplFor.tmplForFunc, context)

    assert("val1val2val3" == res)
  }

}
