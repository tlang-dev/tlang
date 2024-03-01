package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class CallFuncParam(context: Option[ContextContent], params: Option[List[SetAttribute]]) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
