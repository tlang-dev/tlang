package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangModel

case class LangBoolValue(context: Option[ContextContent], value: Boolean) extends LangPrimitiveValue[LangBoolValue] with AstContext {
  override def deepCopy(): LangBoolValue = LangBoolValue(context, if (value) true else false)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangBoolValue]): Int = 0

  override def getElement: LangBoolValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangBoolValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangBoolValue.model
}

object LangBoolValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
