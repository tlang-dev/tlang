package dev.tlang.tlang.ast.tmpl

case class TmplType(var name: TmplID, var generic: Option[TmplGeneric] = None, isArray: Boolean = false) extends DeepCopy {
  override def deepCopy(): TmplType = TmplType(name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false)
}
