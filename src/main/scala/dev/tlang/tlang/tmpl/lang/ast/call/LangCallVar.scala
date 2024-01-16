package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangID}

case class LangCallVar(context: Option[ContextContent], var name: LangID) extends LangCallObjType[LangCallVar] {
  override def deepCopy(): LangCallVar = LangCallVar(context, name.deepCopy().asInstanceOf[LangID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangCallVar]): Int = 0

  override def getElement: LangCallVar = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallVar.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangCallVar.model
}

object LangCallVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}