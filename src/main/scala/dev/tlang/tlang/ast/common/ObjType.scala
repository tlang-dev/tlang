package dev.tlang.tlang.ast.common

import tlang.core.{Null, Type}
import tlang.internal
import tlang.internal.{ClassType, ContextContent}

case class ObjType(context: Null, preType: Option[String], name: Type) extends ValueType {

  override def getContextType: Type = name

  override def getType: Type = ClassType.of(this.getClass)

  override def getContext: Null = context
}
