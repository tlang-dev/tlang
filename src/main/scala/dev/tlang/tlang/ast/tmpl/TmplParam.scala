package dev.tlang.tlang.ast.tmpl

case class TmplParam(name: String, var `type`: TmplType) extends DeepCopy {
  override def deepCopy(): TmplParam = TmplParam(new String(name), `type`.deepCopy())
}
