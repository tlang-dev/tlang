package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.astbuilder.context.ContextContent

case class HelperFuncType(context: Option[ContextContent], params: Option[List[HelperCurrying]], returns: Option[List[ValueType]]) extends ValueType {
  override def getContextType: String = getType

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context
}
