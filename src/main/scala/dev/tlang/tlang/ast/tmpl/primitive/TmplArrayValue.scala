package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplSetAttribute, TmplType}

case class TmplArrayValue(`type`: Option[TmplType] = None, params: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue {
  override def deepCopy(): TmplArrayValue = TmplArrayValue(
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)
}
