package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.TmplBlock

case class LangBlock(context: Option[ContextContent], name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var content: LangFullBlock,
                     scope: Scope = Scope()) extends DomainBlock with TmplBlock[LangBlock] {

  override def deepCopy(): LangBlock =
    LangBlock(context, name, lang, params,
      content.deepCopy(),
      scope)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangBlock]): Int = 0

  override def getElement: LangBlock = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = {
    EntityValue(context, Some(ObjType(context, None, TmplLangAst.tmplLang.name)), Some(List(
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, TmplLangAst.langFullBlock.name)), Operation(context, None, Right(content.toEntity))
      ))))
  }

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  override def getParams: Option[List[HelperParam]] = params

  override def getLang: String = lang

  override def getScope: Scope = scope
}
