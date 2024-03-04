package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterStaticVar(`type`: Type) extends InterValue(InterValueType.StaticVar) {
  override def getType: Type = `type`
}
