package io.sorne.tlang.ast.tmpl

case class TmplVar(var name: String, var `type`: TmplType, var value: TmplExpression) extends TmplExpression
