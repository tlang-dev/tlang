package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterEntity(`type`: Type) extends InterValue(InterValueType.Entity) {

  override def getType: Type = `type`
}
