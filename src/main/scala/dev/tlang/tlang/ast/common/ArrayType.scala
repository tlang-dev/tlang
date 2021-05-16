package dev.tlang.tlang.ast.common

import dev.tlang.tlang.astbuilder.context.ContextContent

case class ArrayType(context: Option[ContextContent], preType: Option[String], name: String) extends ValueType {
  override def getContext: Option[ContextContent] = context

  override def getContextType: String = preType.fold("")(_ + ".") + name

  override def getType: String = name
}
