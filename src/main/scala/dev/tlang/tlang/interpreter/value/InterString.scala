package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterString(`type`: Type) extends InterValue(InterValueType.String) {
  override def getType: Type = `type`

  override def getValue: InterString = this
}
