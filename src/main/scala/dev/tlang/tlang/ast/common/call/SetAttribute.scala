package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class SetAttribute(context: Option[ContextContent], name: Option[String] = None, value: Operation) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
