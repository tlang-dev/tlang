package io.sorne.tlang.ast.tmpl.primitive

import io.sorne.tlang.ast.tmpl.TmplSetAttribute

case class TmplArrayValue(params: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue
