package dev.tlang.tlang.ast.tmpl.primitive

case class TmplLongValue(value: Long) extends TmplPrimitiveValue {
  override def deepCopy(): TmplLongValue = TmplLongValue(value)
}
