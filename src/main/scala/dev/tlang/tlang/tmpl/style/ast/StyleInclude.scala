package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class StyleInclude(context: Option[ContextContent], call: NativeType[CallObject]) extends StyleAttribute[StyleInclude] {
  override def toEntity: AstEntity = AstEntity(context,
    Some(StyleInclude.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "call", call.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = StyleInclude.model

  override def getElement: StyleInclude = this

  override def getType: Type = StyleInclude.modelName

  //  override def deepCopy(): StyleInclude = StyleInclude(context, call.deepCopy().asInstanceOf[NativeType[CallObject]])

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = StyleInclude.model
}

object StyleInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(StyleModel.styleModel), None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("call"), NativeType.modelName),
  )))
}
