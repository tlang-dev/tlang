package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperParam(context: Option[ContextContent], param: Option[String], `type`: ValueType) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
