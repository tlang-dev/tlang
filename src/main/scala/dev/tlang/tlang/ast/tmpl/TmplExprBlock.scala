package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplExprBlock(context: Option[ContextContent], exprs: List[TmplExpression]) extends TmplExprContent with AstContext {
  override def deepCopy(): TmplExprBlock = TmplExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplExpression]))

  override def getContext: Option[ContextContent] = context
}
