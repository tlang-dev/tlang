package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplID, TmplValueAst}

case class TmplStringValue(context: Option[ContextContent], var value: TmplID) extends TmplPrimitiveValue[TmplStringValue] {
  override def deepCopy(): TmplStringValue = TmplStringValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplStringValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: TmplStringValue = this

  override def getType: String = getClass.getName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langString.name)),
    Some(List())
  )
}
