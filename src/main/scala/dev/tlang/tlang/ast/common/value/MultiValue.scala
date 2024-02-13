package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent}

case class MultiValue(context: Null[ContextContent], values: List[Value[_]]) extends ComplexValueStatement[MultiValue] with AstContext {
  override def getElement: MultiValue = this

  override def getType: String = MultiValue.getType

  override def compareTo(value: Value[MultiValue]): Int = 0

  override def getContext: Null[ContextContent] = context
}

object MultiValue extends TLangType {
  override def getType: String = "MultiValue"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
