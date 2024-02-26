package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocLink(context: Null, src: String, name: String) extends DocTextType[DocLink] {

  override def getContext: Null = context

  override def getElement: DocLink = this

  override def getType: Type = DocLink.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocLink.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "src", src),
      BuildLang.createAttrStr(context, "name", name),
    ))
  )

}

object DocLink {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
