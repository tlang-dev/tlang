package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallObj

case class TmplReturn(var call: TmplCallObj) extends TmplExpression {
  override def deepCopy(): TmplReturn = TmplReturn(call.deepCopy())
}
