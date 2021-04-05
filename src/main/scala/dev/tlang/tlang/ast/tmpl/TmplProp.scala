package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplProp(context: Option[ContextContent], var props: List[TmplID]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplProp = TmplProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Option[ContextContent] = context
}
