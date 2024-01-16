package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class LangMultiValue(context: Option[ContextContent], var values: List[LangValueType[_]]) extends LangValueType[LangMultiValue] with AstContext {
  override def deepCopy(): LangMultiValue = LangMultiValue(context, values.map(_.deepCopy().asInstanceOf[LangValueType[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangMultiValue]): Int = 0

  override def getElement: LangMultiValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangMultiValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangMultiValue.model
}

object LangMultiValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}