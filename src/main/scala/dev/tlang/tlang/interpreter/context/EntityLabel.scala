package dev.tlang.tlang.interpreter.context

import tlang.core.{Type, Value}
import tlang.internal.ClassType

case class EntityLabel(tType: Type) extends Value {

  override def getType: Type = ClassType.of(this.getClass)

  override def getValue: Value = this

  def getAttrLabel(name: String): String = tType.getType + "." + name

  def getAttrLabelByIndex(index: Int): String = tType.getType + "." + index.toString
}
