package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterModel(`type`: Type) extends InterValue(InterValueType.Model) {
  override def getType: Type = `type`

  override def getValue: InterModel = this
}
