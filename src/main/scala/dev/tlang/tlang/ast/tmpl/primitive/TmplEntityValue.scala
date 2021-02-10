package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplAttribute

case class TmplEntityValue(params: Option[List[TmplAttribute]], attrs: Option[List[TmplAttribute]]) extends TmplPrimitiveValue
