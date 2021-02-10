package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.Value

case class CallObject(statements: List[CallObjectType]) extends SimpleValueStatement[CallObject] {
  override def getValue: CallObject = this

  override def getType: String = "CallObject"

  override def compareTo(value: Value[CallObject]): Int = 0
}
