package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocAsIs(context: Null[ContextContent], content: String) extends DocContentType[DocAsIs] {
//  override def deepCopy(): DocAsIs = DocAsIs(context, content)

  override def getContext: Null[ContextContent] = context

  override def getType: Type = DocAsIs.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocAsIs.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "content", content)
    ))
  )

//  override def toModel: ModelSetEntity = DocAsIs.model

  override def getElement: DocAsIs = this
}

object DocAsIs {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}