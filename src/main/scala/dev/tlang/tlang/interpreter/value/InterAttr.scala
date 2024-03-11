package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterAttr(`type`: Type) extends InterValue(InterValueType.Attr) {
  override def getType: Type = `type`

  override def getValue: InterAttr = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
