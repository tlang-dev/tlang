package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplCallAst, TmplID}

case class TmplCallFunc(context: Option[ContextContent], var name: TmplID, var currying: Option[List[TmplCallFuncParam]]) extends TmplCallObjType[TmplCallFunc] {
  override def deepCopy(): TmplCallFunc = TmplCallFunc(context, name.deepCopy().asInstanceOf[TmplID],
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallFunc]): Int = 0

  override def getElement: TmplCallFunc = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplCallAst.tmplCallFunc.name)),
    Some(List())
  )
}
