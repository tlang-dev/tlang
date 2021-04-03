package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplReturn(context: Option[ContextContent], var operation: TmplOperation) extends TmplExpression with AstContext {
  override def deepCopy(): TmplReturn = TmplReturn(context, operation.deepCopy())

  override def getContext: Option[ContextContent] = context
}
