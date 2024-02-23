package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, LazyValue, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.ExecFunc
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

import scala.collection.mutable

class TmplForTest extends AnyFunSuite {

  /*test("Simple for") {
    var res = ""

    val array = ArrayValue(Null.empty(), Some(List(ComplexAttribute(Null.empty(), value = Operation(Null.empty(), None, Right(new TLangString(Null.empty(), "val1")))),
      ComplexAttribute(Null.empty(), value = Operation(Null.empty(), None, Right(new TLangString(Null.empty(), "val2")))),
      ComplexAttribute(Null.empty(), value = Operation(Null.empty(), None, Right(new TLangString(Null.empty(), "val3")))))))

    val calledFunc = HelperFunc(Null.empty(), "anyFunc", Some(List(HelperCurrying(Null.empty(), List(HelperParam(Null.empty(), Some("param1"), ObjType(Null.empty(), None, TLangString.getType)))))), None, HelperContent(Null.empty(), Some(List(
      HelperInternalFunc((context: Context) => {
        //        res += context.scopes.last.variables("param1").asInstanceOf[TLangString].getValue
        res += ContextUtils.findVar(context, "param1").get.asInstanceOf[TLangString].getElement
        Right(None)
      })
    ))))

    val call = CallRefFuncObject(Null.empty(), None, Some(List(CallFuncParam(Null.empty(), Some(List(SetAttribute(Null.empty(), value = Operation(Null.empty(), None, Right(LazyValue(Null.empty(), None, Some(TLangString)))))))))), Some(Left(calledFunc)))

    val context = Context(List(Scope(
      variables = mutable.Map("array" -> array),
      refFunctions = mutable.Map("refFunc" -> call)
    )))

    ExecFunc.run(TmplFor.tmplForFunc, context)

    assert("val1val2val3" == res)
  }*/

}
