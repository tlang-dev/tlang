package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplAttribute

case class TmplEntityValue(params: Option[List[TmplAttribute]], attrs: Option[List[TmplAttribute]]) extends TmplPrimitiveValue {
  override def deepCopy(): TmplEntityValue = TmplEntityValue(
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy())) else None)
}
