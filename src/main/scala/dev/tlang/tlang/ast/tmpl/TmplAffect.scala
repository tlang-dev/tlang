package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallObj

case class TmplAffect(var variable: TmplCallObj, var value: TmplCallObj) extends TmplExpression {
  override def deepCopy(): TmplAffect = TmplAffect(variable.deepCopy(), value.deepCopy())
}
