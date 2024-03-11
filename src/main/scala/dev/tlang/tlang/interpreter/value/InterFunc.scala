package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterFunc(`type`: Type) extends InterValue(InterValueType.Function) {
  override def getType: Type = `type`

  override def getValue: InterFunc = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
