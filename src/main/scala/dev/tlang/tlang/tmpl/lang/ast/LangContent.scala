package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import tlang.core.{Null, Type}
import tlang.internal.TmplNode

trait LangContent[TYPE] extends TmplNode[TYPE]


object LangContent {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}