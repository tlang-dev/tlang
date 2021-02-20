package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplID

case class TmplTextValue(var value: TmplID) extends TmplPrimitiveValue {
  override def deepCopy(): TmplTextValue = TmplTextValue(value.deepCopy().asInstanceOf[TmplID])
}
