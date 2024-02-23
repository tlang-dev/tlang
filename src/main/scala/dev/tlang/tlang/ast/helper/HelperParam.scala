package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class HelperParam(context: Null[ContextContent], param: Option[String], `type`: ValueType) extends AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
