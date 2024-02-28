package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangReturn(context: Option[ContextContent], var operation: LangOperation) extends LangExpression[LangReturn] {
  //  override def deepCopy(): LangReturn = LangReturn(context, operation.deepCopy())


  override def toEntity: AstEntity = AstEntity(context,
    Some(LangReturn.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "operation", operation.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangReturn = this

  override def getType: Type = LangReturn.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangReturn.model
}

object LangReturn {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("operation"), LangOperation.modelType),
  )))

}
