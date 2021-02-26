package dev.tlang.tlang.ast.tmpl

case class TmplImpl(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var fors: Option[List[TmplImplFor]], var withs: Option[List[TmplImplWith]], var content: Option[List[TmplContent]] = None) extends TmplContent {
  override def deepCopy(): TmplImpl = TmplImpl(
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (fors.isDefined) Some(fors.get.map(_.deepCopy())) else None,
    if (withs.isDefined) Some(withs.get.map(_.deepCopy())) else None,
    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplContent])) else None
  )
}
