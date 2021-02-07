package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.{TmplID, TmplValueType}

case class TmplCallArray(var name: TmplID, var elem: TmplValueType) extends TmplCallObjType
