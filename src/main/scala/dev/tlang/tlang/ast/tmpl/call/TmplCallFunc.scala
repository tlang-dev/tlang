package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID

case class TmplCallFunc(var name: TmplID, var currying: Option[List[TmplCurryParam]]) extends TmplCallObjType {
  override def deepCopy(): TmplCallFunc = TmplCallFunc(name.deepCopy().asInstanceOf[TmplID],
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)
}
