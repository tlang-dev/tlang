package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallObj
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAffect(context: Option[ContextContent], var variable: TmplCallObj, var value: TmplOperation) extends TmplExpression with AstContext {
  override def deepCopy(): TmplAffect = TmplAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Option[ContextContent] = context
}
