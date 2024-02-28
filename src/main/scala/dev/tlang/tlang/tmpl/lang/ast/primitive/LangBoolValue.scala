package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangBoolValue(context: Option[ContextContent], value: Boolean) extends LangPrimitiveValue[LangBoolValue] {

  override def getContext: Option[ContextContent] = context


  override def getElement: LangBoolValue = this

  override def getType: Type = LangBoolValue.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangBoolValue.model),
    Some(List(
      //      BuildAstTmpl.createAttrBool(context, "value", value)
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangBoolValue.model
}

object LangBoolValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
  )))
}
