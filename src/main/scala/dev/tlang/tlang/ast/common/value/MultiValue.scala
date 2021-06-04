package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class MultiValue(context: Option[ContextContent], values: List[Value[_]]) extends ComplexValueStatement[MultiValue] with AstContext {
  override def getElement: MultiValue = this

  override def getType: String = MultiValue.getType

  override def compareTo(value: Value[MultiValue]): Int = 0

  override def getContext: Option[ContextContent] = context
}

object MultiValue extends TLangType {
  override def getType: String = "MultiValue"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
