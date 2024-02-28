package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallObj
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstContext, AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangAffect(context: Option[ContextContent], var variable: LangCallObj, var value: LangOperation) extends LangExpression[LangAffect] with AstContext {
  //  override def deepCopy(): LangAffect = LangAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def getElement: LangAffect = this

  override def getType: Type = LangAffect.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangAffect.model),
    Some(List(
      BuildAstTmpl.createAttrEntity(context, "variable", Some(LangCallObj.model.getType), variable.toEntity),
      BuildAstTmpl.createAttrEntity(context, "value", Some(LangOperation.model.getType), value.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = LangAffect.model

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangAffect.model
}

object LangAffect {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("variable"), LangCallObj.model.getType),
    BuildAstTmpl.createModelAttrEntity(None, Some("value"), LangOperation.model.getType),
  )))
}
