package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class HelperParam(context: Null[ContextContent], param: Option[String], `type`: ValueType) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
