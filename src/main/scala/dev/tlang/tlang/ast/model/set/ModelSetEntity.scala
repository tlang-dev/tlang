package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.context.Scope

case class ModelSetEntity(context: Option[ContextContent], name: String, params: Option[List[ModelSetAttribute]], attrs: Option[List[ModelSetAttribute]],
                          scope: Scope = Scope()) extends ModelContent[ModelSetEntity] with ModelSetValueType[ModelSetEntity] {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetEntity = this

  override def getType: String = "ModelSetEntity"
}
