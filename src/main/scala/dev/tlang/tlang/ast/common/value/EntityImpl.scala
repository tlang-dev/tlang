package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class EntityImpl(context: Null[ContextContent], var model: Option[ModelSetEntity], `type`: Null[core.String], attrs: Option[List[ComplexAttribute]] = None) extends ComplexValueStatement[EntityImpl] {
  override def compareTo(value: Value[EntityImpl]): Int = 0

  override def getElement: EntityImpl = this

  override def getType: String = "EntityImpl"

  override def getContext: Null[ContextContent] = context
}
