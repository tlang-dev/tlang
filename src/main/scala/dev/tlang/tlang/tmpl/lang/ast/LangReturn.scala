package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangReturn(context: Option[ContextContent], var operation: LangOperation) extends LangExpression[LangReturn] with AstContext {
  override def deepCopy(): LangReturn = LangReturn(context, operation.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangReturn]): Int = 0

  override def getElement: LangReturn = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangReturn.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "operation", operation.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangReturn.model
}

object LangReturn {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))

}
