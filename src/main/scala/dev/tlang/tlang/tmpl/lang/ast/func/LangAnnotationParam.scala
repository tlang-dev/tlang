package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._

case class LangAnnotationParam(context: Option[ContextContent], var name: Option[LangID], var value: LangValueType[_]) extends DeepCopy with LangNode[LangAnnotationParam] with AstContext {
  override def deepCopy(): LangAnnotationParam =
    LangAnnotationParam(context,
      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[LangID]) else None,
      value.deepCopy().asInstanceOf[LangValueType[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAnnotationParam]): Int = 0

  override def getElement: LangAnnotationParam = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotationParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangAnnotationParam.model
}

object LangAnnotationParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
