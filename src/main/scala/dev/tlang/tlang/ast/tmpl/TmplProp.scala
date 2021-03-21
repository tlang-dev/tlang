package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplProp(context: Option[ContextContent], props: List[String]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplProp = TmplProp(context, props.map(new String(_)))

  override def getContext: Option[ContextContent] = context
}
