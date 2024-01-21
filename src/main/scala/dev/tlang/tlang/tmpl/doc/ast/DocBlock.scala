package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplBlock
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocBlock(context: Option[ContextContent], name: String, lang: String,
                    var params: Option[List[HelperParam]], content: DocContent, scope: Scope = Scope()) extends DomainBlock with AnyTmplBlock[DocBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "name", name),
      BuildLang.createAttrStr(context, "lang", lang),
      BuildLang.createAttrEntity(context, "content", content.toEntity)
    ))
  )

  override def toModel: ModelSetEntity = DocBlock.model

  override def compareTo(value: Value[DocBlock]): Int = 0

  override def getElement: DocBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): Any = DocBlock(context, new String(name), new String(lang), params, content.deepCopy(), scope)

  override def getParams: Option[List[HelperParam]] = params

  override def getLang: String = lang

  override def getScope: Scope = scope

  override def getName: String = name
}

object DocBlock {
  val model: ModelSetEntity = ModelSetEntity(None, "DocBlock", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}