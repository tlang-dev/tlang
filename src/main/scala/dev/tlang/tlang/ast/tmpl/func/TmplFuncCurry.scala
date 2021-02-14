package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.TmplParam

case class TmplFuncCurry(var params: Option[List[TmplParam]])
