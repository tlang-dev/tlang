package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.tmpl.{AstContext, AstValue}
import tlang.core.{Type, Value}
import tlang.internal.{ClassType, ContextContent}

case class MultiValue(context: Option[ContextContent], values: List[AstValue]) extends ComplexValueStatement[MultiValue] with AstContext {

  override def getType: Type = MultiValue.getType


  override def getContext: Option[ContextContent] = context
}

object MultiValue extends TLangType {
  override def getType: Type = ClassType.of(classOf[MultiValue])

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
