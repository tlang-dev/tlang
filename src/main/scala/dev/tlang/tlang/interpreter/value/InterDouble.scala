package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterDouble(`type`: Type) extends InterValue(InterValueType.Double) {
  override def getType: Type = `type`

  override def getValue: InterDouble = this
}
