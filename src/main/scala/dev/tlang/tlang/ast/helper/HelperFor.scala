package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class HelperFor(context: Option[ContextContent], variable: String, start: Option[Operation], forType: ForType.forType, array: Operation, body: HelperContent) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)

}
