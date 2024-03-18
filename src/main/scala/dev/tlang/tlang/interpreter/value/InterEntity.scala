package dev.tlang.tlang.interpreter.value

import dev.tlang.tlang.interpreter.Box
import tlang.core
import tlang.core.{Null, Type, Value}

case class InterEntity(`type`: Type, box: Box = Box()) extends InterValue(InterValueType.Entity) {

  override def getType: Type = `type`

  override def getValue: InterEntity = this

  override def getAttr(name: core.String): Null = {
    Null.empty()
  }

  override def getAttrPath(name: String): String = {
    `type`.getType.toString + "/" + name
  }

  override def getAttrPathByPos(pos: Int): String = `type`.getType.toString + "/" + pos
}
