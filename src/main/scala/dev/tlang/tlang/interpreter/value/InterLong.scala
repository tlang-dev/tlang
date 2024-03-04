package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterLong(`type`: Type) extends InterValue(InterValueType.Long) {
  override def getType: Type = `type`
}
