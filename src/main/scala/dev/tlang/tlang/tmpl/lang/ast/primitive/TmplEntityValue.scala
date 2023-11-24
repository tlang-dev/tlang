package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplID, TmplNode, TmplValueAst}

case class TmplEntityValue(context: Option[ContextContent], var name: Option[TmplID], var params: Option[List[TmplNode[_]]], var attrs: Option[List[TmplNode[_]]]) extends TmplPrimitiveValue[TmplEntityValue] {
  override def deepCopy(): TmplEntityValue = TmplEntityValue(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None
  )

  override def compareTo(value: Value[TmplEntityValue]): Int = 0

  override def getElement: TmplEntityValue = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langEntity.name)),
    Some(List())
  )
}
