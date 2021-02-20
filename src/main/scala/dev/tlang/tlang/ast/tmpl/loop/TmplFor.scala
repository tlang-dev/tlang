package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.{TmplExprBlock, TmplExpression}

case class TmplFor(content: TmplExprBlock) extends TmplExpression {
  override def deepCopy(): TmplFor = TmplFor(content.deepCopy())
}
