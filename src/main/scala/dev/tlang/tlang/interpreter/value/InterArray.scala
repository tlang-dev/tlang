package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterArray(`type`: Type) extends InterValue(InterValueType.Array) {
  override def getType: Type = `type`
}
