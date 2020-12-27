package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.TmplValueType

case class TmplCallArray(var name: String, var elem: TmplValueType) extends TmplCallObjType
