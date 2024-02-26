package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocImg(context: Null, src: String, alt: Option[String]) extends DocTextType[DocImg] {

  override def getContext: Null = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocImg.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "src", src),
      BuildLang.createAttrStr(context, "alt", alt.get),
    ))
  )

  override def getElement: DocImg = this

  override def getType: Type = DocImg.modelName
}

object DocImg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}