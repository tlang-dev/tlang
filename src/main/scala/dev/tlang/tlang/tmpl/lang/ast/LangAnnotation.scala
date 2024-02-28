package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplID}

case class LangAnnotation(context: Option[ContextContent], var name: TmplID, var values: Option[List[LangAnnotationParam]]) extends LangContent[LangAnnotation] with Context {
  //  override def deepCopy(): LangAnnotation = LangAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
  //    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def getElement: LangAnnotation = this

  override def getType: Type = LangAnnotation.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangAnnotation.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "name", Some(TmplID.model.getType), name.toEntity),
      BuildAstTmpl.createAttrNullList(context, "values", values.map(_.map(_.toEntity)))
    ))
  )

  //  override def toModel: ModelSetEntity = LangAnnotation.model
}

object LangAnnotation {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("values")),
  )))
}