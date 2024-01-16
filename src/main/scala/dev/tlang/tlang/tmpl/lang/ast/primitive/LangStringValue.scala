package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangID}

case class LangStringValue(context: Option[ContextContent], var value: LangID) extends LangPrimitiveValue[LangStringValue] {
  override def deepCopy(): LangStringValue = LangStringValue(context, value.deepCopy().asInstanceOf[LangID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangStringValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: LangStringValue = this

  override def getType: String = getClass.getSimpleName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangStringValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangStringValue.model
}

object LangStringValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
