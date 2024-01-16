package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangNode, LangType}

case class LangArrayValue(context: Option[ContextContent], var `type`: Option[LangType] = None, var params: Option[List[LangNode[_]]]) extends LangPrimitiveValue[LangArrayValue] {
  override def deepCopy(): LangArrayValue = LangArrayValue(context,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangArrayValue]): Int = 0

  override def getElement: LangArrayValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangArrayValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangArrayValue.model
}

object LangArrayValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
