package dev.tlang.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.TmplID

case class TmplCallFunc(var name: TmplID, var currying: Option[List[TmplCurryParam]]) extends TmplCallObjType
