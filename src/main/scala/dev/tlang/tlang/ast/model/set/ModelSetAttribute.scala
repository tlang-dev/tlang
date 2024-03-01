package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ModelSetAttribute(context: Option[ContextContent], attr: Option[String], value: ModelSetValueType[_]) extends ModelSetValueType[ModelSetAttribute] with AstContext {
  override def getContext: Option[ContextContent] = context


  override def getType: Type = ClassType.of(this.getClass)
}
