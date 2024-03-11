package dev.tlang.tlang.interpreter.value

import tlang.core
import tlang.core.{Type, Value}

case class InterEntity(`type`: Type) extends InterValue(InterValueType.Entity) {

  override def getType: Type = `type`

  override def getValue: InterEntity = this

  override def getAttr(name: core.String): Value = {
    null
  }

  override def getAttrPath(name: String): String = {
    `type`.getType.toString + "/" + name
  }

  override def getAttrPathByPos(pos: Int): String = `type`.getType.toString + "/" + pos
}
