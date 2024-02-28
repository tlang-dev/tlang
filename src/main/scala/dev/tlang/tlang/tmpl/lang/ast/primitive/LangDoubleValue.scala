package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.TLangDouble
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangDoubleValue(context: Option[ContextContent], value: Double) extends LangPrimitiveValue[LangDoubleValue] {

  override def getContext: Option[ContextContent] = context

  override def getElement: LangDoubleValue = this

  override def getType: Type = LangDoubleValue.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangDoubleValue.model),
    Some(List(
      BuildAstTmpl.createAttrDouble(context, "value", new TLangDouble(None, value))
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangDoubleValue.model
}

object LangDoubleValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrDouble(None, Some("value")),
  )))
}
