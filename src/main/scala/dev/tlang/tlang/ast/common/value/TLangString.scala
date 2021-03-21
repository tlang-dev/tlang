package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

class TLangString(value: String) extends PrimitiveValue[String] {
  override def getValue: String = value

  override def getType: String = TLangString.getType

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue

  override def add(value: String): Either[ExecError, String] = Right(this.value + value)

  override def subtract(value: String): Either[ExecError, String] = Left(NotImplemented())

  override def multiply(value: String): Either[ExecError, String] = Left(NotImplemented())

  override def divide(value: String): Either[ExecError, String] = Left(NotImplemented())

  override def modulo(value: String): Either[ExecError, String] = Left(NotImplemented())
}

object TLangString extends TLangType {
  override def getType: String = "String"
}
