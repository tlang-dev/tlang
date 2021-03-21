package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplVar(context: Option[ContextContent], var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var `type`: TmplType, var value: Option[TmplExpression]) extends TmplExpression with AstContext {
  override def deepCopy(): TmplVar = TmplVar(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    `type`.deepCopy(),
    if (value.isDefined) Some(value.get.deepCopy().asInstanceOf[TmplExpression]) else None
  )

  override def getContext: Option[ContextContent] = context
}
