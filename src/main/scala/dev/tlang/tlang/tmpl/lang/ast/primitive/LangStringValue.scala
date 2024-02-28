package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

case class LangStringValue(context: Option[ContextContent], var value: TmplID) extends LangPrimitiveValue[LangStringValue] {
  //  override def deepCopy(): LangStringValue = LangStringValue(context, value.deepCopy().asInstanceOf[TmplID])


  override def getElement: LangStringValue = this

  override def getType: Type = LangStringValue.modelName

  override def toString: String = value.toString

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangStringValue.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangStringValue.model
}

object LangStringValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("value")),
  )))
}
