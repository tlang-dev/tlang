package dev.tlang.tlang.ast.tmpl

case class TmplImplFor(var name: TmplID) extends DeepCopy {
  override def deepCopy(): TmplImplFor = TmplImplFor(name.deepCopy().asInstanceOf[TmplID])
}
