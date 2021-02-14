package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.interpreter.Value

case class CallObject(statements: List[CallObjectType]) extends SimpleValueStatement[CallObject] {
  override def getValue: CallObject = this

  override def getType: String = CallObject.getType

  override def compareTo(value: Value[CallObject]): Int = 0
}

object CallObject extends TLangType {
  override def getType: String = "CallObject"
}
