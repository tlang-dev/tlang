package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterArray(`type`: Type) extends InterValue(InterValueType.Array) {
  override def getType: Type = `type`

  override def getValue: InterArray = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
