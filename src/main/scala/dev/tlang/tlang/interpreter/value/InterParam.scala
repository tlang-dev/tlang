package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterParam(`type`: Type, pos: Int) extends InterValue(InterValueType.Param) {
  override def getType: Type = `type`

  override def getValue: InterParam = this
}
