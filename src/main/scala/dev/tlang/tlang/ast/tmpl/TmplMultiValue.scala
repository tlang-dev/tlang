package dev.tlang.tlang.ast.tmpl

case class TmplMultiValue(var values: List[TmplValueType]) extends TmplValueType
