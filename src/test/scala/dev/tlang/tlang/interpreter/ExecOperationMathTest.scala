package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.TLangLong
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecOperationMathTest extends AnyFunSuite {

  test("Simple addition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(1379 == value.getElement)
  }

  test("Simple subtraction") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.SUBTRACT, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(-1295 == value.getElement)
  }

  test("Simple multiplication") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.MULTIPLY, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(56154 == value.getElement)
  }

  test("Simple division") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.DIVIDE, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(31 == value.getElement)
  }

  test("Minus and multiple") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 7), "var2" -> new TLangLong(None, 2), "var3" -> new TLangLong(None, 5)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.SUBTRACT, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2")))),
        Some((Operator.MULTIPLY, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(-3 == value.getElement)
  }

  test("Multiply with add in parenthesis") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 7), "var2" -> new TLangLong(None, 2), "var3" -> new TLangLong(None, 5)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.MULTIPLY, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2")))),
        Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(49 == value.getElement)
  }

}
