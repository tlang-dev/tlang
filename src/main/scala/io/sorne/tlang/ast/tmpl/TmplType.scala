package io.sorne.tlang.ast.tmpl

case class TmplType(var name: TmplID, var generic: Option[TmplGeneric] = None, isArray: Boolean = false)
