package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam

case class LangAnnotation(context: Option[ContextContent], var name: LangID, var values: Option[List[LangAnnotationParam]]) extends LangContent[LangAnnotation] with AstContext {
  override def deepCopy(): LangAnnotation = LangAnnotation(context, name.deepCopy().asInstanceOf[LangID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAnnotation]): Int = 0

  override def getElement: LangAnnotation = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotation.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangAnnotation.model
}

object LangAnnotation {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}