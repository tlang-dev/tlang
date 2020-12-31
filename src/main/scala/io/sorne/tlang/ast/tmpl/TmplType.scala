package io.sorne.tlang.ast.tmpl

case class TmplType(var name: String, var generic: Option[TmplGeneric] = None, isArray: Boolean = false)
