package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation

case class LangAttribute(context: Option[ContextContent], var attr: Option[LangID], var `type`: Option[LangType], var value: LangOperation) extends LangNode[LangAttribute] {
  override def deepCopy(): LangAttribute = LangAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[LangID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy()
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAttribute]): Int = 0

  override def getElement: LangAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAttribute.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangAttribute.model
}

object LangAttribute {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}