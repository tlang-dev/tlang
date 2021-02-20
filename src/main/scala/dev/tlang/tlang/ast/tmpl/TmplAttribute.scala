package dev.tlang.tlang.ast.tmpl

case class TmplAttribute(attr: Option[TmplID], `type`: Option[TmplType], value: TmplValueType) extends DeepCopy {
  override def deepCopy(): TmplAttribute = TmplAttribute(
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy().asInstanceOf[TmplValueType]
  )
}
