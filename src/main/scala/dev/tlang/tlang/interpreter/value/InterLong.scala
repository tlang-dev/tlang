package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterLong(`type`: Type) extends InterValue(InterValueType.Long) {
  override def getType: Type = `type`

  override def getValue: InterLong = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
