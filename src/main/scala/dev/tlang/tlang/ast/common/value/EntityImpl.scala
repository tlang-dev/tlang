package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.ContextContent

case class EntityImpl(context: Option[ContextContent], var model: Option[ModelSetEntity], `type`: Option[String], attrs: Option[List[ComplexAttribute]] = None) extends ComplexValueStatement[EntityImpl] {
}
