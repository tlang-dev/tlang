package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperArrayType(context: Option[ContextContent], name: String) extends HelperParamType with AstContext {
  override def getContext: Option[ContextContent] = context
}
