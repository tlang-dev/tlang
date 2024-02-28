package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangMultiValue(context: Option[ContextContent], var values: List[LangValueType[_]]) extends LangValueType[LangMultiValue] with Context {
  //  override def deepCopy(): LangMultiValue = LangMultiValue(context, values.map(_.deepCopy().asInstanceOf[LangValueType[_]]))

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangMultiValue.model),
    Some(List(
      BuildAstTmpl.createAttrList(context, "values", values.map(_.toEntity))
    ))
  )

  override def getElement: LangMultiValue = this

  override def getType: Type = LangMultiValue.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangMultiValue.model
}

object LangMultiValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("values")),
  )))
}