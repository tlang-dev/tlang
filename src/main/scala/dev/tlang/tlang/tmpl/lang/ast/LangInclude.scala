package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangInclude(context: Option[ContextContent], calls: List[CallObject]) extends LangExpression[LangInclude] {
  //  override def deepCopy(): LangInclude = LangInclude(context, calls)

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangInclude.model),
    Some(List(
      //      BuildLang.createArray(context, "calls", calls.map(_.toEntity))
    ))
  )

  //  override def toModel: ModelSetEntity = LangInclude.model

  override def getElement: LangInclude = this

  override def getType: Type = LangInclude.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangInclude.model
}

object LangInclude {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("calls")),
  )))
}
