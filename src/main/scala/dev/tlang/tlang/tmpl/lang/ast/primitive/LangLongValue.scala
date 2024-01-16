package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangModel

case class LangLongValue(context: Option[ContextContent], value: Long) extends LangPrimitiveValue[LangLongValue] with AstContext {
  override def deepCopy(): LangLongValue = LangLongValue(context, value)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangLongValue]): Int = this.value.compareTo(value.getElement.value)

  override def getElement: LangLongValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangLongValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangLongValue.model
}

object LangLongValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
