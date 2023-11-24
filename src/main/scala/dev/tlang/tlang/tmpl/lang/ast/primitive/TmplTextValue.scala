package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplID, TmplValueAst}

case class TmplTextValue(context: Option[ContextContent], var value: TmplID) extends TmplPrimitiveValue[TmplTextValue] with AstContext {
  override def deepCopy(): TmplTextValue = TmplTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplTextValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: TmplTextValue = this

  override def getType: String = getClass.getName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langText.name)),
    Some(List())
  )
}
