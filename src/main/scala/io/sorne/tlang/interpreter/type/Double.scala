package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class Double(value: scala.Double) extends Value[scala.Double] {
  override def getValue: scala.Double = value

  override def getType: Predef.String = getClass.getName

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getValue)
}
