package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocCodeBlock(context: Null[ContextContent], lang: String, code: String) extends DocTextType[DocCodeBlock] {

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocCodeBlock.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "language", lang),
      BuildLang.createAttrStr(context, "code", code)
    ))
  )


  override def getElement: DocCodeBlock = this

  override def getType: Type = DocCodeBlock.modelName
}

object DocCodeBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}