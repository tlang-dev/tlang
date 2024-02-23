package dev.tlang.tlang.ast.common

import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class ArrayType(context: Null[ContextContent], preType: Option[String], name: String) extends ValueType {
  override def getContext: Null[ContextContent] = context

  override def getContextType: Type = ManualType(preType.getOrElse(""), name)

  override def getType: Type = ClassType.of(this.getClass)
}
