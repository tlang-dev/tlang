package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangEntityValue(context: Option[ContextContent], var name: Option[TmplID], var params: Option[List[TmplNode[_]]], var attrs: Option[List[TmplNode[_]]]) extends LangPrimitiveValue[LangEntityValue] {
  //  override def deepCopy(): LangEntityValue = LangEntityValue(context,
  //    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
  //    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
  //    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None
  //  )

  override def getElement: LangEntityValue = this

  override def getType: Type = LangEntityValue.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangEntityValue.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "name", if (name.isDefined) name.get.toEntity else new TmplStringId(context, "").toEntity),
      //      BuildAstTmpl.createArray(context, "params", params.map(_.map(_.toEntity)).getOrElse(List())),
      //      BuildAstTmpl.createArray(context, "attrs", attrs.map(_.map(_.toEntity)).getOrElse(List())),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangEntityValue.model
}

object LangEntityValue {

  val name: String = "LangEntity"

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrNull(None, Some("attrs")),
  )))
}
