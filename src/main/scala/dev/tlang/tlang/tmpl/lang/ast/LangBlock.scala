package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.{AstAnyTmplBlock, AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangBlock(context: Option[ContextContent], name: String, langs: List[String],
                     var params: Option[List[NativeType[HelperParam]]],
                     var content: LangFullBlock,
                     scope: Scope = Scope()) extends AstAnyTmplBlock {

  //  override def deepCopy(): LangBlock =
  //    LangBlock(context, name, langs, params,
  //      content.deepCopy(),
  //      scope)

  override def getElement: LangBlock = this

  override def getType: Type = LangBlock.modelName

  override def toEntity: AstEntity = {
    AstEntity(context, Some(LangBlock.model), Some(List(
      BuildAstTmpl.createAttrStr(context, "name", name),
      BuildAstTmpl.createAttrList(context, "langs", langs.map(value => new TLangString(None, value))),
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      BuildAstTmpl.createAttrEntity(context, "content", Some(LangFullBlock.modelType), content.toEntity)
    )))
  }

  //  override def toModel: ModelSetEntity = LangBlock.model

  //  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: List[String] = langs

  //  override def getScope: Scope = scope

  override def getName: String = name

  override def getContext: Option[ContextContent] = context

  override def toModel: AstModel = LangBlock.model
}

object LangBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("name")),
    BuildAstTmpl.createModelAttrStr(None, Some("langs")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrEntity(None, Some("content"), LangFullBlock.modelType),
  )))
}
