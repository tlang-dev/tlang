package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplImplFor(context: Option[ContextContent], var props: Option[TmplProp] = None, var types: List[TmplType]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplImplFor = TmplImplFor(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context
}
