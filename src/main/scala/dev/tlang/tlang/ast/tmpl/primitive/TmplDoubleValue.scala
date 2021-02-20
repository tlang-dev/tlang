package dev.tlang.tlang.ast.tmpl.primitive

case class TmplDoubleValue(value: Double) extends TmplPrimitiveValue {
  override def deepCopy(): TmplDoubleValue = TmplDoubleValue(value)
}
