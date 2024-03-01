package dev.tlang.tlang.ast.common

import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ArrayType(context: Option[ContextContent], preType: Option[String], name: String) extends ValueType {
  override def getContext: Option[ContextContent] = context

  override def getContextType: Type = ManualType(preType.getOrElse(""), name)

  override def getType: Type = ClassType.of(this.getClass)
}
