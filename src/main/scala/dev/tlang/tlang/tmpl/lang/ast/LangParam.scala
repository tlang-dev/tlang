package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class LangParam(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var name: LangID, var `type`: Option[LangType]) extends LangNode[LangParam] {
  override def deepCopy(): LangParam = LangParam(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    name.deepCopy().asInstanceOf[LangID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangParam]): Int = 0

  override def getElement: LangParam = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangParam.model
}

object LangParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}