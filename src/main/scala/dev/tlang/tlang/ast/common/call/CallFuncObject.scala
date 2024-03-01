package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class CallFuncObject(context: Option[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]]) extends CallObjectType with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
