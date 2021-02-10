package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplSetAttribute
import dev.tlang.tlang.ast.tmpl.TmplSetAttribute

case class TmplCurryParam(var params: Option[List[TmplSetAttribute]])
