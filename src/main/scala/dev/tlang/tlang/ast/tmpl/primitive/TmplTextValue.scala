package dev.tlang.tlang.ast.tmpl.primitive

import io.sorne.tlang.ast.tmpl.TmplID

case class TmplTextValue(var value: TmplID) extends TmplPrimitiveValue
