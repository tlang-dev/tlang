package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplParam}

case class TmplFuncCurry(var params: Option[List[TmplParam]]) extends DeepCopy {
  override def deepCopy(): TmplFuncCurry = TmplFuncCurry(
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)
}
