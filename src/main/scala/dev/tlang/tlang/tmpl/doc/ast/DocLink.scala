package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocLink(context: Option[ContextContent], src: String, name: String) extends DocTextType[DocLink] {
  override def deepCopy(): DocLink = DocLink(context, new String(src), new String(name))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocLink]): Int = 0

  override def getElement: DocLink = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "src", src),
      BuildLang.createAttrStr(context, "name", name),
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
