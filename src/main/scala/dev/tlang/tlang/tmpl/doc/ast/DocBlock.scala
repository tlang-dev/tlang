package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstEntity, AstModel, BuildAstTmpl}
import tlang.core.{Array, Type}
import tlang.internal.{ContextContent, DomainBlock}

case class DocBlock(context: Option[ContextContent], name: String, langs: Array,
                    var params: Option[List[NativeType[HelperParam]]], content: DocContent, scope: Scope = Scope()) extends DomainBlock with AnyTmplInterpretedBlock[DocBlock] {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DocBlock.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "name", name),
      //      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value)).toEntity)),
      //      BuildLang.createAttrEntity(context, "content", content.toEntity)
    ))
  )

  //  override def toModel: ModelSetEntity = DocBlock.model

  override def getType: Type = DocBlock.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): Any = DocBlock(context, new String(name), langs.map(new String(_)), params, content.deepCopy(), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.statement))

  override def getScope: Scope = scope

  override def getElement: DocBlock = this
}

object DocBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("name")),
    BuildAstTmpl.createModelAttrArray(None, Some("langs")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrEntity(None, Some("content"), DocContent.model.getType),
  )))
}