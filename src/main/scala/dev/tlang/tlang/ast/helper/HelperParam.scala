package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperParam(context: Option[ContextContent], param: Option[String], `type`: HelperParamType) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
