package dev.tlang.tlang.ast.tmpl

case class TmplGeneric(var types: List[TmplType]) extends DeepCopy {
  override def deepCopy(): TmplGeneric = TmplGeneric(types.map(_.deepCopy()))
}
