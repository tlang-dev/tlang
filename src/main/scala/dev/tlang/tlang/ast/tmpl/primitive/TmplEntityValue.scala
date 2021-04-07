package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplID, TmplSetAttribute}

case class TmplEntityValue(name: TmplID, attrs: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue {
  override def deepCopy(): TmplEntityValue = TmplEntityValue(
    name.deepCopy().asInstanceOf[TmplID],
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy())) else None)
}
