package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocLink(context: Null[ContextContent], src: String, name: String) extends DocTextType[DocLink] {
  override def deepCopy(): DocLink = DocLink(context, new String(src), new String(name))

  override def getContext: Null[ContextContent] = context

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

  override def toModel: ModelSetEntity = DocLink.model
}

object DocLink {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocLink", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
