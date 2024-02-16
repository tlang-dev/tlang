package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocImg(context: Null[ContextContent], src: String, alt: Option[String]) extends DocTextType[DocImg] {
  override def deepCopy(): DocImg = DocImg(context, new String(src), if (alt.isDefined) Some(new String(alt.get)) else None)

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "src", src),
      BuildLang.createAttrStr(context, "alt", alt.get),
    ))
  )

  override def toModel: ModelSetEntity = DocImg.model

  override def compareTo(value: Value[DocImg]): Int = 0

  override def getElement: DocImg = this

  override def getType: String = getClass.getSimpleName
}

object DocImg {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocImg", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}