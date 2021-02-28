package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.condition.ConditionBlock
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperIf(context: Option[ContextContent], condition: ConditionBlock, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context
}
