package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocPlainText(context: Option[ContextContent], text: String) extends DocTextType[DocPlainText] {
  override def deepCopy(): DocPlainText = DocPlainText(context, new String(text))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocPlainText]): Int = 0

  override def getElement: DocPlainText = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "text", text)
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
