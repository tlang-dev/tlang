package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.TmplSetAttribute

case class TmplCurryParam(var params: Option[List[TmplSetAttribute]])
