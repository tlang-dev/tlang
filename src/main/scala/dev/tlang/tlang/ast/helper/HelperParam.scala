package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class HelperParam(context: Option[ContextContent], param: Option[String], `type`: ValueType) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
