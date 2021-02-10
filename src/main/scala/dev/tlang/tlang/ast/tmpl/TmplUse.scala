package dev.tlang.tlang.ast.tmpl

case class TmplUse(var parts: List[TmplID], var alias: Option[TmplID] = None)
