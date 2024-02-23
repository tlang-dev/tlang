package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecOperationMathTest extends AnyFunSuite {

  /*test("Simple addition") {
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

  test("Simple modulo") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.MODULO, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(35 == value.getElement)
  }

  test("Simple addition (double)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangDouble(None, 42.23), "var2" -> new TLangDouble(None, 1337.64)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangDouble]
    assert(42.23 + 1337.64 == value.getElement)
  }

  test("Simple subtraction (double)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangDouble(None, 42.64), "var2" -> new TLangDouble(None, 1337.23)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.SUBTRACT, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangDouble]
    assert(42.64 - 1337.23 == value.getElement)
  }

  test("Simple multiplication (double)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangDouble(None, 42.64), "var2" -> new TLangDouble(None, 1337.23)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.MULTIPLY, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangDouble]
    assert(42.64 * 1337.23 == value.getElement)
  }

  test("Simple division (double)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangDouble(None, 1337.23), "var2" -> new TLangDouble(None, 42.64)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.DIVIDE, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangDouble]
    assert(1337.23 / 42.64 == value.getElement)
  }

  test("Simple modulo (double)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangDouble(None, 1337.64), "var2" -> new TLangDouble(None, 42.23)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.MODULO, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangDouble]
    assert(1337.64 % 42.23 == value.getElement)
  }

  test("Concat strings") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "foo"), "var2" -> new TLangString(None, "bar")))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangString]
    assert("foobar" == value.getElement)
  }

  test("Concat arrays") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new ArrayValue(None, Some(List(ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangLong(None, 1)))), ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangLong(None, 2))))))),
      "var2" -> new ArrayValue(None, Some(List(ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangLong(None, 3)))), ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangLong(None, 4)))))))))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[ArrayValue].tbl.get
    assert(1 == value.head.value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(2 == value(1).value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(3 == value(2).value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(4 == value.last.value.content.toOption.get.asInstanceOf[TLangLong].getElement)
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

  test("Multiply with add and subtract in parenthesis") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 7), "var2" -> new TLangLong(None, 2), "var3" -> new TLangLong(None, 5), "var4" -> new TLangLong(None, 4)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.SUBTRACT, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.MULTIPLY, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))),
        Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4")))))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangLong]
    assert(45 == value.getElement)
  }

  test("Five levels priority => (7 - 2) * 5 < 2 || 7 > 7 + 5 / 2") {
    val context = Context(List(Scope(variables = mutable.Map(
      "var1" -> new TLangLong(None, 7),
      "var2" -> new TLangLong(None, 2),
      "var3" -> new TLangLong(None, 5),
    ))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.SUBTRACT, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.MULTIPLY, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))),
        Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2")))),
          Some((Operator.OR, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
            Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
              Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))),
                Some((Operator.DIVIDE, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2")))))))))))))))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }*/

}
