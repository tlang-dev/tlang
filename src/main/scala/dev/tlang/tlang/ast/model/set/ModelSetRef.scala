package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetRef(context: Option[ContextContent], refs: List[String], currying: Option[List[ModelSetRefCurrying]])
  extends ModelSetValueType[ModelSetRef] with ModelSetRefValue with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetRef = this

  override def getType: String = "ModelSetRef"
}
