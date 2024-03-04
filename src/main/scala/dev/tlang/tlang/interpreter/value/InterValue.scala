package dev.tlang.tlang.interpreter.value

import tlang.core.Type

abstract class InterValue(valueType: InterValueType.Value) {
  def getName: String = getType.getSimpleType.toString

  def getFullName: String = getType.getType.toString

  def getType: Type

  def hasAttr: Boolean = false

  def getAttr(name: String): Option[InterValue] = None
}
