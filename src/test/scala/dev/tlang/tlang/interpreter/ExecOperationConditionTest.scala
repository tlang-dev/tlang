package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangLong}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecOperationConditionTest extends AnyFunSuite {

  test("Simple true condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Simple false condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.AND, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks, true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.AND, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two blocks, false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.AND, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two false blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.AND, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.OR, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks, true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.OR, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.OR, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two false blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.OR, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with >= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.GREATER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("True condition with >= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.GREATER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with >=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.GREATER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with <= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.LESSER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("True condition with <= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.LESSER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with <=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.LESSER_OR_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.NOT_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))),
      Some((Operator.NOT_EQUAL, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two embedded operations with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.AND, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two embedded operations false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.AND, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two embedded operations true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.AND, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two false embedded operations with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.AND, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true embedded operations with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.OR, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two embedded operations false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.OR, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two embedded operations true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.OR, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two false embedded operations with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))))),
      Some((Operator.OR, Operation(None, None, Left(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two embedded operations with comparator compared by OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42), "var3" -> new TLangLong(None, 23), "var4" -> new TLangLong(None, 64)))))
    val statement = Operation(None, None, Left(
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))), Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.OR, Operation(None, None, Left(
        Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))), Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4"))))))))
      )))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two embedded operations, true and false, with comparator compared by OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42), "var3" -> new TLangLong(None, 64), "var4" -> new TLangLong(None, 23)))))
    val statement = Operation(None, None, Left(
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))), Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.OR, Operation(None, None, Left(
        Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))), Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4"))))))))
      )))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two false embedded operations with comparator compared by OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337), "var3" -> new TLangLong(None, 64), "var4" -> new TLangLong(None, 23)))))
    val statement = Operation(None, None, Left(
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))), Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.OR, Operation(None, None, Left(
        Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))), Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4"))))))))
      )))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two embedded operations with comparator compared by AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42), "var3" -> new TLangLong(None, 23), "var4" -> new TLangLong(None, 64)))))
    val statement = Operation(None, None, Left(
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))), Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.AND, Operation(None, None, Left(
        Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))), Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4"))))))))
      )))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two embedded operations, true and false with comparator compared by AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42), "var3" -> new TLangLong(None, 64), "var4" -> new TLangLong(None, 23)))))
    val statement = Operation(None, None, Left(
      Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1")))), Some((Operator.GREATER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))))),
      Some((Operator.AND, Operation(None, None, Left(
        Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var3")))), Some((Operator.LESSER, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var4"))))))))
      )))))
    val value = ExecOperation.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

}
