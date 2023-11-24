package dev.tlang.tlang.tmpl.lang.ast

case class TmplVal() extends DeepCopy {
  override def deepCopy(): TmplVal = TmplVal()
}
