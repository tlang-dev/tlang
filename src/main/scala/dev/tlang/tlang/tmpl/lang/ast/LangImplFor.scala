package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangImplFor(context: Option[ContextContent], var props: Option[LangProp] = None, var types: List[LangType]) extends AstTmplNode {
  //  override def deepCopy(): LangImplFor = LangImplFor(context,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangImplFor.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Some(props.get.toEntity) else None,
      //        None
      //      ),
      //      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  //  override def toModel: ModelSetEntity = LangImplFor.model

  override def getElement: LangImplFor = this

  override def getType: Type = LangImplFor.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangImplFor.model
}

object LangImplFor {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
    BuildAstTmpl.createModelAttrNull(None, Some("types")),
  )))
}