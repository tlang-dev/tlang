package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.condition.{Condition, ConditionBlock}
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangLong}
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecConditionBlockTest extends AnyFunSuite {

  test("Simple true condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Simple false condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.AND), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.AND), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two blocks false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.AND), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two false blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.AND), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.OR), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.OR), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two blocks false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.OR), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two false blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))))),
      Some(ConditionLink.OR), Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.GREATER), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.GREATER), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.LESSER), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.LESSER), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with >= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.GREATER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("True condition with >= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.GREATER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with >=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.GREATER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with <= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.LESSER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("True condition with <= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.LESSER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with <=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 1337), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.LESSER_OR_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("True condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 1337)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.NOT_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("False condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangLong(None, 42), "var2" -> new TLangLong(None, 42)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), Some(ConditionType.NOT_EQUAL), Some(CallObject(None, List(CallVarObject(None, "var2")))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true conditions with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.AND), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two conditions false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.AND), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two conditions true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.AND), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two false conditions with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.AND), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

  test("Two true conditions with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.OR), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two conditions false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, true)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.OR), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two conditions true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, true), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None,
      CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.OR), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getElement)
  }

  test("Two false conditions with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(None, false), "var2" -> new TLangBool(None, false)))))
    val statement = ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var1"))), link = Some(ConditionLink.OR), nextBlock = Some(ConditionBlock(None, Right(Condition(None, CallObject(None, List(CallVarObject(None, "var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getElement)
  }

}
