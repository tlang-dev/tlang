package io.sorne.tlang.ast.tmpl.primitive

import io.sorne.tlang.ast.tmpl.{TmplSetAttribute, TmplType}

case class TmplArrayValue(`type`: Option[TmplType] = None, params: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue
