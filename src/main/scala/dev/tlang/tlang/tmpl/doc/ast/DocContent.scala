package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocContent(context: Option[ContextContent], contents: List[DocContentType[_]]) extends LangNode[DocContent] {
  override def deepCopy(): DocContent = DocContent(context, contents.map(_.deepCopy().asInstanceOf[DocContentType[_]]))

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = DocContent.model

  override def compareTo(value: Value[DocContent]): Int = 0

  override def getElement: DocContent = this

  override def getType: String = getClass.getSimpleName
}

object DocContent {
  val model: ModelSetEntity = ModelSetEntity(None, "DocContent", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}