package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import tlang.core.{Null, Type, Value}
import tlang.internal.{Context, ClassType, ContextContent}

case class MultiValue(context: Null, values: List[Value]) extends ComplexValueStatement[MultiValue] with Context {

  override def getType: Type = MultiValue.getType


  override def getContext: Null = context
}

object MultiValue extends TLangType {
  override def getType: Type = ClassType.of(classOf[MultiValue])

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
