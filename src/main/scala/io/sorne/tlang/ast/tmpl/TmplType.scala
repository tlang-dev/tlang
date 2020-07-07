package io.sorne.tlang.ast.tmpl

case class TmplType(name: String, generic: Option[TmplGeneric], isArray: Boolean = false)
