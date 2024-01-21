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
import dev.tlang.tlang.tmpl.AnyTmplBlock

case class LangBlock(context: Option[ContextContent], name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var content: LangFullBlock,
                     scope: Scope = Scope()) extends DomainBlock with AnyTmplBlock[LangBlock] {

  override def deepCopy(): LangBlock =
    LangBlock(context, name, lang, params,
      content.deepCopy(),
      scope)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangBlock]): Int = 0

  override def getElement: LangBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = {
    EntityValue(context, Some(ObjType(context, None, LangBlock.name)), Some(List(
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, LangFullBlock.name)), Operation(context, None, Right(content.toEntity))
      ))))
  }

  override def toModel: ModelSetEntity = LangBlock.model

  override def getParams: Option[List[HelperParam]] = params

  override def getLang: String = lang

  override def getScope: Scope = scope

  override def getName: String = name
}

object LangBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, "LangBlock", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
