package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplNode}

case class LangImplWith(context: Option[ContextContent], var props: Option[LangProp] = None, var types: List[LangType]) extends TmplNode[LangImplWith] with Context {
  //  override def deepCopy(): LangImplWith = LangImplWith(context,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    types.map(_.deepCopy()))

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangImplWith.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "props",
      ////        props,
      //        None
      //      ),
      //      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  //  override def toModel: ModelSetEntity = LangImplWith.model

  override def getContext: Option[ContextContent] = context

  override def getElement: LangImplWith = this

  override def getType: Type = LangImplWith.modelName
}

object LangImplWith {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
    BuildAstTmpl.createModelAttrNull(None, Some("types")),
  )))
}
