package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocSec(context: Null[ContextContent], title: String, content: DocContent) extends DocContentType[DocSec] {
//  override def deepCopy(): DocSec = DocSec(context, new String(title), content.deepCopy())

  override def getContext: Null[ContextContent] = context

  override def getElement: DocSec = this

  override def getType: Type = DocSec.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocSec.modelName)),
    Some(List())
  )

}

object DocSec {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
