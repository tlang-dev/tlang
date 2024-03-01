package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangProp(context: Option[ContextContent], var props: List[TmplID]) extends AstTmplNode {
  //  override def deepCopy(): LangProp = LangProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Option[ContextContent] = context

  override def getElement: LangProp = this

  override def getType: Type = LangProp.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangProp.model),
    Some(List(
      //      BuildAstTmpl.createAttrList(context, "props", props.map(_.toEntity))
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangProp.model
}

object LangProp {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrArray(None, Some("props")),
  )))
}
