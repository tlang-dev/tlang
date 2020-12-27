package io.sorne.tlang.ast.tmpl

case class TmplType(var name: String, var generic: Option[TmplGeneric], isArray: Boolean = false)
