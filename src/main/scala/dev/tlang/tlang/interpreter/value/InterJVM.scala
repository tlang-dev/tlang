package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterJVM(`type`: Type) extends InterValue(InterValueType.JVM) {
  override def getType: Type = `type`
}
