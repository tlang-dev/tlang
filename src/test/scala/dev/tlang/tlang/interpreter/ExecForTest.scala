package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.{ForType, HelperContent, HelperFor, HelperInternalFunc}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ExecForTest extends AnyFunSuite {

  test("For 1 to 10") {
    var count = 0
    var variable = 0
    val forStatement = HelperFor(None, "index",
      Some(Operation(None, None, Right(new TLangLong(None, 1)))),
      ForType.TO,
      Operation(None, None, Right(new TLangLong(None, 10))),
      HelperContent(None, Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.head.variables("index").asInstanceOf[TLangLong].getElement.toInt
        Right(None)
      })))))
    ExecFor.run(forStatement, Context(List()))
    assert(10 == count)
    assert(10 == variable)
  }

  test("For 0 until 10") {
    var count = 0
    var variable = 0
    val forStatement = HelperFor(None, "index",
      Some(Operation(None, None, Right(new TLangLong(None, 0)))),
      ForType.UNTIL,
      Operation(None, None, Right(new TLangLong(None, 10))),
      HelperContent(None, Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.head.variables("index").asInstanceOf[TLangLong].getElement.toInt
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
    val forStatement = HelperFor(None, "elem",
      Some(Operation(None, None, Right(new TLangLong(None, 0)))),
      ForType.IN,
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "myArray"))))),
      HelperContent(None, Some(List(HelperInternalFunc((context: Context) => {
        count += 1
        variable = context.scopes.last.variables("_i").asInstanceOf[TLangLong].getElement.toInt
        array.addOne(context.scopes.last.variables("elem").asInstanceOf[TLangString].getElement)
        Right(None)
      })))))
    val scope = Scope(variables = mutable.Map("myArray" -> ArrayValue(None, Some(List(
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "One")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "Two")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "Three"))))
    )))))
    ExecFor.run(forStatement, Context(List(scope)))
    assert(3 == count)
    assert(2 == variable)
    assert("One" == array.head)
    assert("Two" == array(1))
    assert("Three" == array.last)
  }

}
