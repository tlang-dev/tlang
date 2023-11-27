package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplValueAst}

case class TmplDoubleValue(context: Option[ContextContent], value: Double) extends TmplPrimitiveValue[TmplDoubleValue] with AstContext {
  override def deepCopy(): TmplDoubleValue = TmplDoubleValue(context, value)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplDoubleValue]): Int = 0

  override def getElement: TmplDoubleValue = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langDouble.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
