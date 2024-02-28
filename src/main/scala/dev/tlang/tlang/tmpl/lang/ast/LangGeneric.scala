package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class LangGeneric(context: Option[ContextContent], var types: List[LangType]) extends TmplNode[LangGeneric] {
  //  override def deepCopy(): LangGeneric = LangGeneric(context, types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangGeneric.model),
    Some(List(
      //      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  //  override def toModel: ModelSetEntity = LangGeneric.model

  override def getElement: LangGeneric = this

  override def getType: Type = LangGeneric.modelName
}

object LangGeneric {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrArray(None, Some("types")),
  )))
}
