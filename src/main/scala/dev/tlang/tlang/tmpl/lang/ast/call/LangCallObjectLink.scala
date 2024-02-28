package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangCallObjectLink(context: Option[ContextContent], var link: String = ".", var call: LangCallObjType[_]) extends AstTmplNode {
  //  override def deepCopy(): LangCallObjectLink = LangCallObjectLink(context, link, call.deepCopy().asInstanceOf[LangCallObjType[_]])

  override def getType: Type = LangCallObjectLink.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallObjectLink.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "link", link),
      //      BuildAstTmpl.createAttrEntity(context, "call", call.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangCallObjectLink = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangCallObjectLink.model
}

object LangCallObjectLink {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("link")),
    //    BuildAstTmpl.createModelAttrEntity(None, Some("call"), LangCallObjType.model),
  )))
}
