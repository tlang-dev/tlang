package dev.tlang.tlang.ast.tmpl

case class TmplUse(var parts: List[TmplID], var alias: Option[TmplID] = None) extends DeepCopy {
  override def deepCopy(): TmplUse = TmplUse(parts.map(_.deepCopy().asInstanceOf[TmplID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)
}
