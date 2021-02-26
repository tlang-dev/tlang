package dev.tlang.tlang.ast.tmpl

case class TmplImplWith(var name: TmplID) extends DeepCopy {
  override def deepCopy(): TmplImplWith = TmplImplWith(name.deepCopy().asInstanceOf[TmplID])
}
