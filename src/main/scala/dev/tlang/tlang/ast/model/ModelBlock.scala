package dev.tlang.tlang.ast.model

import dev.tlang.tlang.tmpl.{AstContext, AstModel}
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent, DomainBlock}

case class ModelBlock(context: Option[ContextContent], content: Option[List[ModelContent[_]]]) extends DomainBlock with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
