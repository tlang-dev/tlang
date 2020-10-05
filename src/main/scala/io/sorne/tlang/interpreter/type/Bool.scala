package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class Bool(value: Boolean) extends Value[Boolean] {
  override def getValue: Boolean = value

  override def getType: String = getClass.getName

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)
}
