package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

class TLangBool(context: Option[ContextContent], value: Boolean) extends PrimitiveValue[Boolean]() with AstContext {
  override def getElement: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getElement)

  override def toString: String = if (getElement) "true" else "false"

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented())

  override def subtract(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented())

  override def multiply(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented())

  override def divide(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented())

  override def modulo(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented())
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"
}
