package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplNode, TmplType, TmplValueAst}

case class TmplArrayValue(context: Option[ContextContent], var `type`: Option[TmplType] = None, var params: Option[List[TmplNode[_]]]) extends TmplPrimitiveValue[TmplArrayValue] {
  override def deepCopy(): TmplArrayValue = TmplArrayValue(context,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplArrayValue]): Int = 0

  override def getElement: TmplArrayValue = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langArray.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
