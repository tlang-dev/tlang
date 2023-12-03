package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocList(context: Option[ContextContent], order: String, contents: List[DocContent]) extends DocTextType[DocList] {
  override def deepCopy(): DocList = DocList(context, new String(order), contents.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocList]): Int = 0

  override def getElement: DocList = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "order", order),
      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}