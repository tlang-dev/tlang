package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplValueType
import dev.tlang.tlang.ast.tmpl.{TmplID, TmplValueType}

case class TmplCallArray(var name: TmplID, var elem: TmplValueType) extends TmplCallObjType
