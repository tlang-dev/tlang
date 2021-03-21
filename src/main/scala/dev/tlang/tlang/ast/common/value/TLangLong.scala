package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.{ExecError, Value}

class TLangLong(value: Long) extends PrimitiveValue[Long] {
  override def getValue: Long = value

  override def getType: String = TLangLong.getType

  override def compareTo(value: Value[Long]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue.toString

  override def add(value: Long): Either[ExecError, Long] = Right(this.value + value)

  override def subtract(value: Long): Either[ExecError, Long] = Right(this.value - value)

  override def multiply(value: Long): Either[ExecError, Long] = Right(this.value * value)

  override def divide(value: Long): Either[ExecError, Long] = Right(this.value / value)

  override def modulo(value: Long): Either[ExecError, Long] = Right(this.value % value)

}

object TLangLong extends TLangType {
  override def getType: String = "Long"
}
