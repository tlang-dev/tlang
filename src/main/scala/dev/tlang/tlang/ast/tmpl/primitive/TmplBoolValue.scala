package dev.tlang.tlang.ast.tmpl.primitive

case class TmplBoolValue(value: Boolean) extends TmplPrimitiveValue {
  override def deepCopy(): TmplBoolValue = TmplBoolValue(if (value) true else false)
}
