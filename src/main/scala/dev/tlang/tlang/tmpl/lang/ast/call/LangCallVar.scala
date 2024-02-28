package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

case class LangCallVar(context: Option[ContextContent], var name: TmplID) extends LangCallObjType[LangCallVar] {
  //  override def deepCopy(): LangCallVar = LangCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallVar.model),
    Some(List(
      //          BuildAstTmpl.createAttrEntity(context, "name", name.toEntity)
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangCallVar = this

  override def getType: Type = LangCallVar.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangCallVar.model
}

object LangCallVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
  )))
}