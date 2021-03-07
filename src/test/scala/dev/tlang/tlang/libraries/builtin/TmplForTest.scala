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

    val array = ArrayValue(None, Some(List(SimpleAttribute(None, value = new TLangString(None, "val1")),
      SimpleAttribute(None, value = new TLangString(None, "val2")),
      SimpleAttribute(None, value = new TLangString(None, "val3")))))

    val calledFunc = HelperFunc(None, "anyFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("param1"), HelperObjType(None, TLangString.getType)))))), None, HelperContent(None, Some(List(
      HelperInternalFunc((context: Context) => {
        //        res += context.scopes.last.variables("param1").asInstanceOf[TLangString].getValue
        res += ContextUtils.findVar(context, "param1").get.asInstanceOf[TLangString].getElement
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(None, None, Some(List(CallFuncParam(None, Some(List(SetAttribute(None, value = LazyValue(None, None, Some(TLangString)))))))), Some(Left(calledFunc)))

    val context = Context(List(Scope(
      variables = mutable.Map("array" -> array),
      refFunctions = mutable.Map("refFunc" -> call)
    )))

    ExecFunc.run(TmplFor.tmplForFunc, context)

    assert("val1val2val3" == res)
  }

}
