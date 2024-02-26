package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DocContent(context: Null, contents: List[DocContentType[_]]) extends TmplNode[DocContent] {
//  override def deepCopy(): DocContent = DocContent(context, contents.map(_.deepCopy().asInstanceOf[DocContentType[_]]))

  override def getContext: Null = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocContent.modelName)),
    Some(List(
      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def getType: Type = DocContent.modelName

  override def getElement: DocContent = this
}

object DocContent {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}