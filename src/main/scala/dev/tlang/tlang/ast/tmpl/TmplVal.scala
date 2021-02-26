package dev.tlang.tlang.ast.tmpl

case class TmplVal() extends DeepCopy {
  override def deepCopy(): TmplVal =  TmplVal()
}
