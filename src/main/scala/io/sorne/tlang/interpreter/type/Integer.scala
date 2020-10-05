package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class Integer(value: Int) extends Value[Int] {
  override def getValue: Int = value

  override def getType: String = getClass.getName

  override def compareTo(value: Value[Int]): Int = this.value.compareTo(value.getValue)
}
