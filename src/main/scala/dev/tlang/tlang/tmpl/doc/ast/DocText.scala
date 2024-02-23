package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocText(context: Null[ContextContent], text: DocTextType[_]) extends DocContentType[DocText] {
//  override def deepCopy(): DocText = DocText(context, text.deepCopy().asInstanceOf[DocTextType[_]])

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocText.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "text", text.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = DocText.model

  override def getElement: DocText = this

  override def getType: Type = DocText.modelName
}

object DocText {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
