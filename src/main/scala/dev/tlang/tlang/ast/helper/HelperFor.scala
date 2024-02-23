package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class HelperFor(context: Null[ContextContent], variable: String, start: Option[Operation], forType: ForType.forType, array: Operation, body: HelperContent) extends HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)

}
