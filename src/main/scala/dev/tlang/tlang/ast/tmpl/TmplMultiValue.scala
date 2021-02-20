package dev.tlang.tlang.ast.tmpl

case class TmplMultiValue(var values: List[TmplValueType]) extends TmplValueType {
  override def deepCopy(): TmplMultiValue = TmplMultiValue(values.map(_.deepCopy().asInstanceOf[TmplValueType]))
}
