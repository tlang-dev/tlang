package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplSetAttribute, TmplType}

case class TmplArrayValue(`type`: Option[TmplType] = None, params: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue
