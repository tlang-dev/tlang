package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.TmplSetAttribute

case class TmplCurryParam(params: Option[List[TmplSetAttribute]])
