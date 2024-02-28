package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangLongValue(context: Option[ContextContent], value: Long) extends LangPrimitiveValue[LangLongValue] {

  override def getContext: Option[ContextContent] = context

  override def getElement: LangLongValue = this

  override def getType: Type = LangLongValue.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangLongValue.model),
    Some(List(
      //      BuildAstTmpl.createAttrLong(context, "value", value)
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangLongValue.model
}

object LangLongValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrLong(None, Some("value")),
  )))
}
