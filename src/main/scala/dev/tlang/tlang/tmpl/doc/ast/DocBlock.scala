package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplBlock
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst

case class DocBlock(context: Option[ContextContent], content: DocContent) extends DomainBlock with TmplBlock[DocBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  override def compareTo(value: Value[DocBlock]): Int = 0

  override def getElement: DocBlock = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): Any = DocBlock(context, content.deepCopy())
}
