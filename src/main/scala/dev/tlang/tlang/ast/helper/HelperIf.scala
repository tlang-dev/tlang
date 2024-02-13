package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class HelperIf(context: Null[ContextContent], condition: Operation, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context
}
