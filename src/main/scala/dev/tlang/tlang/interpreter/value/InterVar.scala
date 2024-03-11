package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterVar(`type`: Type, pos: Int) extends InterValue(InterValueType.Var) {
  override def getType: Type = `type`

  override def getValue: InterVar = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
