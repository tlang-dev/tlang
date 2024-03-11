package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

case class InterStaticVar(`type`: Type) extends InterValue(InterValueType.StaticVar) {
  override def getType: Type = `type`

  override def getValue: InterStaticVar = this

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
