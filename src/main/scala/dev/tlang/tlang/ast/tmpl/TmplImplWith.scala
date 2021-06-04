package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.ContextContent

case class TmplImplWith(context: Option[ContextContent], var props: Option[TmplProp] = None, var types: List[TmplType]) extends DeepCopy {
  override def deepCopy(): TmplImplWith = TmplImplWith(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    types.map(_.deepCopy()))
}
