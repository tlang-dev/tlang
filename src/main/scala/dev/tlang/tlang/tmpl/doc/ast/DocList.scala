package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocList(context: Null, order: String, contents: List[DocContent]) extends DocTextType[DocList] {
//  override def deepCopy(): DocList = DocList(context, new String(order), contents.map(_.deepCopy()))

  override def getContext: Null = context

  override def getElement: DocList = this

  override def getType: Type = DocList.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocList.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "order", order),
//      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

}

object DocList {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
