package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class ModelSetAttribute(context: Null, attr: Option[String], value: ModelSetValueType[_]) extends ModelSetValueType[ModelSetAttribute] with Context {
  override def getContext: Null = context


  override def getType: Type = ClassType.of(this.getClass)
}
