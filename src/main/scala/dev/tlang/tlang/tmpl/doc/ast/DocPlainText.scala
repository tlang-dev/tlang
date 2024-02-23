package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocPlainText(context: Null[ContextContent], text: String) extends DocTextType[DocPlainText] {

  override def getContext: Null[ContextContent] = context

  override def getElement: DocPlainText = this

  override def getType: Type = DocPlainText.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocPlainText.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "text", text)
    ))
  )

}

object DocPlainText {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
