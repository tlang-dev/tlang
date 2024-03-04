package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterModel(`type`: Type) extends InterValue(InterValueType.Model) {
  override def getType: Type = `type`
}
