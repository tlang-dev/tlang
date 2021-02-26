package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID

case class TmplCallVar(var name: TmplID) extends TmplCallObjType {
  override def deepCopy(): TmplCallVar = TmplCallVar(name.deepCopy().asInstanceOf[TmplID])
}
