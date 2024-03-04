package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterDouble(`type`: Type) extends InterValue(InterValueType.Double) {
  override def getType: Type = `type`
}
