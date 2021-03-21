package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

case class ArrayValue(tbl: Option[List[SimpleAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getValue: ArrayValue = this

  override def getType: String = ArrayValue.getType

  override def compareTo(value: Value[ArrayValue]): Int = 0

  override def add(value: ArrayValue): Either[ExecError, ArrayValue] = {
    if (tbl.isEmpty && value.tbl.isEmpty) Right(ArrayValue(None))
    else Right(ArrayValue(Some(tbl.getOrElse(List()) ++: value.tbl.getOrElse(List()))))
  }

  override def subtract(value: ArrayValue): Either[ExecError, ArrayValue] = Left(NotImplemented())

  override def multiply(value: ArrayValue): Either[ExecError, ArrayValue] = Left(NotImplemented())

  override def divide(value: ArrayValue): Either[ExecError, ArrayValue] = Left(NotImplemented())

  override def modulo(value: ArrayValue): Either[ExecError, ArrayValue] = Left(NotImplemented())
}

object ArrayValue extends TLangType {
  override def getType: String = "ArrayValue"
}
