package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.Null
import tlang.internal.ContextContent

case class HelperFuncType(context: Null[ContextContent], params: Option[List[HelperCurrying]], returns: Option[List[ValueType]]) extends ValueType {
  override def getContextType: String = getType

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context
}
