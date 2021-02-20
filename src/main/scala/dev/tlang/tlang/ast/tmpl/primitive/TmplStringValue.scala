package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplID

case class TmplStringValue(var value: TmplID) extends TmplPrimitiveValue {
  override def deepCopy(): TmplStringValue = TmplStringValue(value.deepCopy().asInstanceOf[TmplID])
}
