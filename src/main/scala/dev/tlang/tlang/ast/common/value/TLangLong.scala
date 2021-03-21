package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.{ExecError, Value}

class TLangLong(context: Option[ContextContent], value: Long) extends PrimitiveValue[Long] with AstContext {
  override def getElement: Long = value

  override def getType: String = TLangLong.getType

  override def compareTo(value: Value[Long]): Int = this.value.compareTo(value.getElement)

  override def toString: String = getElement.toString

  override def getContext: Option[ContextContent] = context

  override def add(value: Long): Either[ExecError, Long] = Right(this.value + value)

  override def subtract(value: Long): Either[ExecError, Long] = Right(this.value - value)

  override def multiply(value: Long): Either[ExecError, Long] = Right(this.value * value)

  override def divide(value: Long): Either[ExecError, Long] = Right(this.value / value)

  override def modulo(value: Long): Either[ExecError, Long] = Right(this.value % value)

}

object TLangLong extends TLangType {
  override def getType: String = "Long"
}
