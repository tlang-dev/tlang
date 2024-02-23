package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class HelperFuncType(context: Null[ContextContent], params: Option[List[HelperCurrying]], returns: Option[List[ValueType]]) extends ValueType {
  override def getContextType: Type = getType

  override def getType: Type = ClassType.of(this.getClass)

  override def getContext: Null[ContextContent] = context
}
