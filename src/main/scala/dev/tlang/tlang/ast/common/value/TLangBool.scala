package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

class TLangBool(value: Boolean) extends PrimitiveValue[Boolean]() {
  override def getValue: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)

  override def toString: String = if(getValue) "true" else "false"

  override def add(value: Boolean): Either[ExecError, Boolean] = Left(NotImplemented())

  override def subtract(value: Boolean): Either[ExecError, Boolean] = Left(NotImplemented())

  override def multiply(value: Boolean): Either[ExecError, Boolean] = Left(NotImplemented())

  override def divide(value: Boolean): Either[ExecError, Boolean] = Left(NotImplemented())

  override def modulo(value: Boolean): Either[ExecError, Boolean] = Left(NotImplemented())
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"
}
