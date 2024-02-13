package dev.tlang.tlang.ast.common

import tlang.core.Null
import tlang.internal.ContextContent

case class ArrayType(context: Null[ContextContent], preType: Option[String], name: String) extends ValueType {
  override def getContext: Null[ContextContent] = context

  override def getContextType: String = preType.fold("")(_ + ".") + name

  override def getType: String = name
}
