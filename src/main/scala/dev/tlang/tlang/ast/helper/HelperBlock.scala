package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.DomainBlock
import tlang.core.{Array, Null}
import tlang.internal.{AstContext, ContextContent}

case class HelperBlock(context: Null[ContextContent], funcs: Null[Array[HelperFunc]]) extends DomainBlock with AstContext {
  override def getContext: Null[ContextContent] = context
}
