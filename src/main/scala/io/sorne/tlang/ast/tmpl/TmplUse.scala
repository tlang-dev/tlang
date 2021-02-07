package io.sorne.tlang.ast.tmpl

case class TmplUse(var parts: List[TmplID], var alias: Option[TmplID] = None)
