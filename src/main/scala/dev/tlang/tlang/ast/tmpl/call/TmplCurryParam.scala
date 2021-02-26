package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplSetAttribute}

case class TmplCurryParam(var params: Option[List[TmplSetAttribute]]) extends DeepCopy {
  override def deepCopy(): TmplCurryParam = TmplCurryParam(if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)
}
