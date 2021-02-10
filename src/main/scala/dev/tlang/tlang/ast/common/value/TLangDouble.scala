package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.Value

class TLangDouble(value: Double) extends PrimitiveValue[Double] {
  override def getValue: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue.toString

}

object TLangDouble extends TLangType {
  override def getType: String = "Double"
}
