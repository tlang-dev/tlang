package dev.tlang.tlang.interpreter.value

import tlang.core.{Type, Value}

abstract class InterValue(valueType: InterValueType.Value) extends Value {
  def getName: String = getType.getSimpleType.toString

  def getFullName: String = getType.getType.toString

  def getType: Type

  def getAttrPath(name: String): String

  def getAttrPathByPos(pos: Int): String
}
