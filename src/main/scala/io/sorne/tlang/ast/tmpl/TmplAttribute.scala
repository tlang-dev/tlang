package io.sorne.tlang.ast.tmpl

case class TmplAttribute(attr: Option[String], `type`: Option[TmplType], value: TmplValueType)
