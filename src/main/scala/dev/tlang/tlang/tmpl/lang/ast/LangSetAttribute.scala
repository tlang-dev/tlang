package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation

case class LangSetAttribute(context: Option[ContextContent], var name: Option[LangID], var value: LangOperation) extends LangNode[LangSetAttribute] {
  override def deepCopy(): LangSetAttribute = LangSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[LangID]) else None, value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangSetAttribute]): Int = 0

  override def getElement: LangSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSetAttribute.name)),
    Some(List(

    )))

  override def toModel: ModelSetEntity = LangSetAttribute.model
}

object LangSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
