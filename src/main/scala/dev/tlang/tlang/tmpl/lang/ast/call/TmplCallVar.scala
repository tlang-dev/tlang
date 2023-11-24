package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplCallAst, TmplID}

case class TmplCallVar(context: Option[ContextContent], var name: TmplID) extends TmplCallObjType[TmplCallVar] {
  override def deepCopy(): TmplCallVar = TmplCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallVar]): Int = 0

  override def getElement: TmplCallVar = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplCallAst.tmplCallObjVar.name)),
    Some(List())
  )
}
