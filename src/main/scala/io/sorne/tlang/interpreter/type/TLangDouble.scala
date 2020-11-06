package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class TLangDouble(value: Double) extends Value[Double] {
  override def getValue: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getValue)
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"
}
