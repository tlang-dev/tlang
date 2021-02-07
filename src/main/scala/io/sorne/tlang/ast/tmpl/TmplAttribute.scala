package io.sorne.tlang.ast.tmpl

case class TmplAttribute(attr: Option[TmplID], `type`: Option[TmplType], value: TmplValueType)
