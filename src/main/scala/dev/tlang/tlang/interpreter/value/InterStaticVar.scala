package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterStaticVar(`type`: Type) extends InterValue(InterValueType.StaticVar) {
  override def getType: Type = `type`

  override def getValue: InterStaticVar = this
}
