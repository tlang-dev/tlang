package io.sorne.tlang.ast.tmpl

case class TmplUse(var parts: List[String], var alias: Option[String] = None)
