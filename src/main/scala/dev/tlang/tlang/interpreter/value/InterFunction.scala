package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterFunction(`type`: Type) extends InterValue(InterValueType.Function) {
  override def getType: Type = `type`
}
