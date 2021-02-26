package dev.tlang.tlang.ast.tmpl

case class TmplExprBlock(exprs: List[TmplExpression]) extends TmplExprContent {
  override def deepCopy(): TmplExprBlock = TmplExprBlock(exprs.map(_.deepCopy().asInstanceOf[TmplExpression]))
}
