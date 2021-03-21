package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.{ExecError, Value}

class TLangDouble(value: Double) extends PrimitiveValue[Double] {
  override def getValue: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue.toString

  override def add(value: Double): Either[ExecError, Double] = Right(this.value + value)

  override def subtract(value: Double): Either[ExecError, Double] = Right(this.value - value)

  override def multiply(value: Double): Either[ExecError, Double] = Right(this.value * value)

  override def divide(value: Double): Either[ExecError, Double] = Right(this.value / value)

  override def modulo(value: Double): Either[ExecError, Double] = Right(this.value % value)
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"
}
