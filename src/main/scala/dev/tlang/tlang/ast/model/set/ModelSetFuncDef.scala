package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class ModelSetFuncDef(context: Null, params: Option[List[ModelSetAttribute]] = None, returns: Option[List[ModelSetAttribute]] = None) extends ModelSetValueType[ModelSetFuncDef] with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
