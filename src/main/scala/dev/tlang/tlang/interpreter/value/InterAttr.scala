package dev.tlang.tlang.interpreter.value

import tlang.core.Type

case class InterAttr(`type`: Type) extends InterValue(InterValueType.Attr) {
  override def getType: Type = `type`
}
