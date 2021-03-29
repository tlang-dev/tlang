package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{TmplExpression, TmplSimpleValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCallObj(context: Option[ContextContent], var calls: List[TmplCallObjType]) extends TmplSimpleValueType with TmplExpression with AstContext {
  override def deepCopy(): TmplCallObj = TmplCallObj(context, calls.map(_.deepCopy().asInstanceOf[TmplCallObjType]))

  override def getContext: Option[ContextContent] = context
}
