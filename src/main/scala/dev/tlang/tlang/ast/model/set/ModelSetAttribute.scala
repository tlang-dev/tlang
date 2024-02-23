package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class ModelSetAttribute(context: Null[ContextContent], attr: Option[String], value: ModelSetValueType[_]) extends ModelSetValueType[ModelSetAttribute] with AstContext {
  override def getContext: Null[ContextContent] = context


  override def getType: Type = ClassType.of(this.getClass)
}
