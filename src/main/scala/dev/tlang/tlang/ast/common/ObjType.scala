package dev.tlang.tlang.ast.common

import tlang.core.{Null, Type}
import tlang.internal
import tlang.internal.ContextContent

case class ObjType(context: Null[ContextContent], preType: Option[String], name: Type) extends ValueType {

  override def getContextType: String = preType.fold("")(_ + ".") + name

  override def getType: String = name.getSimpleType.toString

  override def getContext: Null[internal.ContextContent] = context
}
