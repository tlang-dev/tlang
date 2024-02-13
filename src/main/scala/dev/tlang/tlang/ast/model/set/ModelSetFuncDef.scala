package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ModelSetFuncDef(context: Null[ContextContent], params: Option[List[ModelSetAttribute]] = None, returns: Option[List[ModelSetAttribute]] = None) extends ModelSetValueType[ModelSetFuncDef] with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getElement: ModelSetFuncDef = this

  override def getType: String = "ModelSetFuncDef"
}
