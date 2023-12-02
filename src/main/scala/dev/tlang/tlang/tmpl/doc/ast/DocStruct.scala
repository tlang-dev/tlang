package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocStruct(context: Option[ContextContent], level: Int, title: String, content: Option[DocContent]) extends DocContentType[DocStruct] {
  override def deepCopy(): DocStruct = DocStruct(context, level, new String(title),
    if (content.isDefined) Some(content.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocStruct]): Int = 0

  override def getElement: DocStruct = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrInt(context, "level", level),
      BuildLang.createAttrStr(context, "title", title),
      BuildLang.createAttrEntity(context, "content", content.get.toEntity)
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
