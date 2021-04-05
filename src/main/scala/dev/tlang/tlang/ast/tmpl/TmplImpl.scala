package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplImpl(context: Option[ContextContent], var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var fors: Option[TmplImplFor], var withs: Option[TmplImplWith], var content: Option[List[TmplContent]] = None) extends TmplContent with AstContext {
  override def deepCopy(): TmplImpl = TmplImpl(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (fors.isDefined) Some(fors.get.deepCopy()) else None,
    if (withs.isDefined) Some(withs.get.deepCopy()) else None,
    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplContent])) else None
  )

  override def getContext: Option[ContextContent] = context
}
