package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplMultiValue(context: Option[ContextContent], var values: List[TmplValueType[_]]) extends TmplValueType[TmplMultiValue] with AstContext {
  override def deepCopy(): TmplMultiValue = TmplMultiValue(context, values.map(_.deepCopy().asInstanceOf[TmplValueType[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplMultiValue]): Int = 0

  override def getElement: TmplMultiValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langMultiValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}