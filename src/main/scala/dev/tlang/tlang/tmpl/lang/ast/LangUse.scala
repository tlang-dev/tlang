package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangUse(context: Option[ContextContent], var parts: List[TmplID], var alias: Option[TmplID] = None) extends AstTmplNode {
  //  override def deepCopy(): LangUse = LangUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
  //    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def getElement: LangUse = this

  override def getType: Type = LangUse.modelName


  override def toEntity: AstEntity = {
    AstEntity(context,
      Some(LangUse.model),
      Some(List(
        //        BuildAstTmpl.createAttrList(context, "parts", parts.map(part => part.toEntity)),
        //        BuildLang.createAttrNull(context, "alias",
        //          if (alias.isDefined) Null.of(alias.get.toEntity) else Null.empty(),
        //          None
        //        ),
      )
      ))
  }

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangUse.model
}

object LangUse {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrArray(None, Some("parts")),
    BuildAstTmpl.createModelAttrNull(None, Some("alias")),
  )))
}

