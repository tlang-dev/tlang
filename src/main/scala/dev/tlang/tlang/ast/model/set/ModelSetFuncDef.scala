package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ModelSetFuncDef(context: Option[ContextContent], params: Option[List[ModelSetAttribute]] = None, returns: Option[List[ModelSetAttribute]] = None) extends ModelSetValueType[ModelSetFuncDef] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
