package io.sorne.tlang.ast.tmpl

case class TmplSetAttribute(var name: Option[TmplID], var value: TmplValueType)
