package io.sorne.tlang.ast.common.value

import io.sorne.tlang.interpreter.Value

class TLangDouble(value: Double) extends PrimitiveValue[Double] {
  override def getValue: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getValue)
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"
}
