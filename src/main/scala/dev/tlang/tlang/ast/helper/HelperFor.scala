package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class HelperFor(context: Null, variable: String, start: Option[Operation], forType: ForType.forType, array: Operation, body: HelperContent) extends HelperStatement with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)

}
