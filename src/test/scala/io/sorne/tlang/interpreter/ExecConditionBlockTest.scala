package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.call.{HelperCallObject, HelperCallVarObject}
import io.sorne.tlang.ast.helper.{ConditionLink, ConditionType, HelperCondition, HelperConditionBlock}
import io.sorne.tlang.interpreter.`type`.{TLangBool, TLangInt}
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecConditionBlockTest extends AnyFunSuite {

  test("Simple true condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Simple false condition") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two true blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.AND), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two blocks true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.AND), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two blocks false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.AND), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two false blocks with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.AND), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two true blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.OR), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two blocks true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.OR), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two blocks false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.OR), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two false blocks with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))))),
      Some(ConditionLink.OR), Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with =") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(1337), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.GREATER),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with >") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.GREATER),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.LESSER),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with <") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(1337), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.LESSER),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with >= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.GREATER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("True condition with >= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(1337), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.GREATER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with >=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.GREATER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with <= (equal numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.LESSER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("True condition with <= (different numbers)") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.LESSER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with <=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(1337), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.LESSER_OR_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("True condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(1337)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.NOT_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("False condition with !=") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangInt(42), "var2" -> new TLangInt(42)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), Some(ConditionType.NOT_EQUAL),Some(HelperCallObject(List(HelperCallVarObject("var2"))) ))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two true conditions with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.AND), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two conditions false and true with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.AND), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two conditions true and false with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.AND), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two false conditions with AND") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.AND), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

  test("Two true conditions with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.OR), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two conditions false and true with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(true)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.OR), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two conditions true and false with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(true), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(
      HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.OR), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(value.getValue)
  }

  test("Two false conditions with OR") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangBool(false), "var2" -> new TLangBool(false)))))
    val statement = HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var1"))), link = Some(ConditionLink.OR), nextBlock = Some(HelperConditionBlock(Right(HelperCondition(HelperCallObject(List(HelperCallVarObject("var2"))))))))))
    val value = ExecConditionBlock.run(statement, context).toOption.get.get.head.asInstanceOf[TLangBool]
    assert(!value.getValue)
  }

}
