package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperFor(context: Option[ContextContent], variable: String, start: Option[Operation], forType: ForType.forType, array: Operation, body: HelperContent) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context
}
