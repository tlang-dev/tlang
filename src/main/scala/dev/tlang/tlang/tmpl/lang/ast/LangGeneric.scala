package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class LangGeneric(context: Option[ContextContent], var types: List[LangType]) extends LangNode[LangGeneric] {
  override def deepCopy(): LangGeneric = LangGeneric(context, types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangGeneric]): Int = 0

  override def getElement: LangGeneric = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangGeneric.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangGeneric.model
}

object LangGeneric {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

