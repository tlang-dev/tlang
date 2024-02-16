package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocContent(context: Null[ContextContent], contents: List[DocContentType[_]]) extends TmplNode[DocContent] {
  override def deepCopy(): DocContent = DocContent(context, contents.map(_.deepCopy().asInstanceOf[DocContentType[_]]))

  override def getContext: Null[ContextContent] = context

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

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}