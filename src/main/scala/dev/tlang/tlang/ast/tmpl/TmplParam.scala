package dev.tlang.tlang.ast.tmpl

case class TmplParam(var name: TmplID, var `type`: TmplType) extends DeepCopy {
  override def deepCopy(): TmplParam = TmplParam(name.deepCopy().asInstanceOf[TmplID], `type`.deepCopy())
}
