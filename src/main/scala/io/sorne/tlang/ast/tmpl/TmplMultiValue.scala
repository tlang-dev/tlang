package io.sorne.tlang.ast.tmpl

case class TmplMultiValue(var values: List[TmplValueType]) extends TmplValueType
