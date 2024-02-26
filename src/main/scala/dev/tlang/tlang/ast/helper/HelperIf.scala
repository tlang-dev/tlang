package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class HelperIf(context: Null, condition: Operation, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement with AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
