package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{DeepCopy, LangModel, LangNode}

case class LangCallObjectLink(context: Option[ContextContent], var link: String = ".", var call: LangCallObjType[_]) extends DeepCopy with LangNode[LangCallObjectLink] {
  override def deepCopy(): LangCallObjectLink = LangCallObjectLink(context, link, call.deepCopy().asInstanceOf[LangCallObjType[_]])

  override def compareTo(value: Value[LangCallObjectLink]): Int = 0

  override def getElement: LangCallObjectLink = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObjectLink.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangCallObjectLink.model
}

object LangCallObjectLink {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
