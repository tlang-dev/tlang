package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterResource(`type`: Type) extends InterValue(InterValueType.String) {
  override def getType: Type = `type`
}
