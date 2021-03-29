package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperIf(context: Option[ContextContent], condition: Operation, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context
}
