package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.TmplValueType

case class TmplCallArray(name: String, elem: TmplValueType) extends TmplCallObjType
