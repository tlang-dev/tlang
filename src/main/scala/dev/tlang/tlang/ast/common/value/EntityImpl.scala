package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class EntityImpl(context: Option[ContextContent], var model: Option[ModelSetEntity], `type`: Option[String], attrs: Option[List[ComplexAttribute]] = None) extends ComplexValueStatement[EntityImpl] {
  override def compareTo(value: Value[EntityImpl]): Int = 0

  override def getElement: EntityImpl = this

  override def getType: String = "EntityImpl"

  override def getContext: Option[ContextContent] = context
}
