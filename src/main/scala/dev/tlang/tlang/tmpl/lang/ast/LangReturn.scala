package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangReturn(context: Null[ContextContent], var operation: LangOperation) extends LangExpression[LangReturn] with AstContext {
  override def deepCopy(): LangReturn = LangReturn(context, operation.deepCopy())


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangReturn.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "operation", operation.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangReturn.model

  override def getContext: Null[ContextContent] = context
}

object LangReturn {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("operation"), ModelSetType(Null.empty(), LangOperation.modelType)),
  )))

}
