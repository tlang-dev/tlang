package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{TmplID, TmplValueType}

case class TmplCallArray(var name: TmplID, var elem: TmplValueType) extends TmplCallObjType {
  override def deepCopy(): TmplCallArray = TmplCallArray(name.deepCopy().asInstanceOf[TmplID], elem.deepCopy().asInstanceOf[TmplValueType])
}
