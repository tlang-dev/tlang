package dev.tlang.tlang.ast.common

import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ObjType(context: Option[ContextContent], preType: Option[String], name: Type) extends ValueType {

  override def getContextType: Type = name

  override def getType: Type = ClassType.of(this.getClass)

  override def getContext: Option[ContextContent] = context
}
