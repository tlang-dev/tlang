package io.sorne.tlang.ast.tmpl

case class TmplVar(name:String, `type`: TmplType, value: TmplExpression) extends TmplExpression
