package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetFuncDef(context: Option[ContextContent], params: Option[List[ModelSetAttribute]] = None, returns: Option[List[ModelSetAttribute]] = None) extends ModelSetValueType[ModelSetFuncDef] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetFuncDef = this

  override def getType: String = "ModelSetFuncDef"
}
