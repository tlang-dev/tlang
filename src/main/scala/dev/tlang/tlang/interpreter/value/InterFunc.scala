package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterFunc(`type`: Type) extends InterValue(InterValueType.Function) {
  override def getType: Type = `type`

  override def getValue: InterFunc = this
}
