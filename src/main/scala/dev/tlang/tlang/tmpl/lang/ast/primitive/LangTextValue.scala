package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl, TmplID}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangTextValue(context: Option[ContextContent], var value: TmplID) extends LangPrimitiveValue[LangTextValue] {
  //  override def deepCopy(): LangTextValue = LangTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context


  override def getElement: LangTextValue = this

  override def getType: Type = LangTextValue.modelName

  override def toString: String = value.toString

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangTextValue.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangTextValue.model
}

object LangTextValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("value")),
  )))
}