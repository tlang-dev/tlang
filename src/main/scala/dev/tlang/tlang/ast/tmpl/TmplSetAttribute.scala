package dev.tlang.tlang.ast.tmpl

case class TmplSetAttribute(var name: Option[TmplID], var value: TmplValueType)
