package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class HelperFor(context: Null[ContextContent], variable: String, start: Option[Operation], forType: ForType.forType, array: Operation, body: HelperContent) extends HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context
}
