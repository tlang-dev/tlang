package dev.tlang.tlang.ast.tmpl

case class TmplSetAttribute(var name: Option[TmplID], var value: TmplValueType) extends DeepCopy {
  override def deepCopy(): TmplSetAttribute = TmplSetAttribute(
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy().asInstanceOf[TmplValueType])
}
