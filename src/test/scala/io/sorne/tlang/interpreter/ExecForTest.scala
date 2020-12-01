package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.common.call.{CallObject, CallVarObject}
import io.sorne.tlang.ast.common.value.{ArrayValue, SimpleAttribute, TLangLong, TLangString}
import io.sorne.tlang.ast.helper.{ForType, HelperContent, HelperFor, HelperInternalFunc}
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ExecForTest extends AnyFunSuite {

  test("For 1 to 10") {
    var count = 0
    var variable = 0
    val forStatement = HelperFor("index",
      Some(new TLangLong(1)),
      ForType.TO,
      new TLangLong(10),
      HelperContent(Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.head.variables("index").asInstanceOf[TLangLong].getValue.toInt
        Right(None)
      })))))
    ExecFor.run(forStatement, Context(List()))
    assert(10 == count)
    assert(10 == variable)
  }

  test("For 0 until 10") {
    var count = 0
    var variable = 0
    val forStatement = HelperFor("index",
      Some(new TLangLong(0)),
      ForType.UNTIL,
      new TLangLong(10),
      HelperContent(Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.head.variables("index").asInstanceOf[TLangLong].getValue.toInt
        Right(None)
      })))))
    ExecFor.run(forStatement, Context(List()))
    assert(10 == count)
    assert(9 == variable)
  }

  test("For in array") {
    var count = 0
    var variable = 0
    val array = ListBuffer.empty[String]
    val forStatement = HelperFor("elem",
      Some(new TLangLong(0)),
      ForType.IN,
      CallObject(List(CallVarObject("myArray"))),
      HelperContent(Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.last.variables("_i").asInstanceOf[TLangLong].getValue.toInt
        array.addOne(context.scopes.last.variables("elem").asInstanceOf[TLangString].getValue)
        Right(None)
      })))))
    val scope = Scope(variables = mutable.Map("myArray" -> ArrayValue(Some(List(
      SimpleAttribute(None, None, new TLangString("One")),
      SimpleAttribute(None, None, new TLangString("Two")),
      SimpleAttribute(None, None, new TLangString("Three"))
    )))))
    ExecFor.run(forStatement, Context(List(scope)))
    assert(3 == count)
    assert(2 == variable)
    assert("One" == array.head)
    assert("Two" == array(1))
    assert("Three" == array.last)
  }

}
