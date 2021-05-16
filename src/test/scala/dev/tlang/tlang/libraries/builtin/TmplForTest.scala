package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, LazyValue, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.ExecFunc
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class TmplForTest extends AnyFunSuite {

  test("Simple for") {
    var res = ""

    val array = ArrayValue(None, Some(List(ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, "val1")))),
      ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, "val2")))),
      ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, "val3")))))))

    val calledFunc = HelperFunc(None, "anyFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("param1"), ObjType(None, None, TLangString.getType)))))), None, HelperContent(None, Some(List(
      HelperInternalFunc((context: Context) => {
        //        res += context.scopes.last.variables("param1").asInstanceOf[TLangString].getValue
        res += ContextUtils.findVar(context, "param1").get.asInstanceOf[TLangString].getElement
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(None, None, Some(List(CallFuncParam(None, Some(List(SetAttribute(None, value = Operation(None, None, Right(LazyValue(None, None, Some(TLangString)))))))))), Some(Left(calledFunc)))

    val context = Context(List(Scope(
      variables = mutable.Map("array" -> array),
      refFunctions = mutable.Map("refFunc" -> call)
    )))

    ExecFunc.run(TmplFor.tmplForFunc, context)

    assert("val1val2val3" == res)
  }

}
