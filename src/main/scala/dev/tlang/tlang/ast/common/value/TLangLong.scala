package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value
import io.sorne.tlang.interpreter.Value

class TLangLong(value: Long) extends PrimitiveValue[Long] {
  override def getValue: Long = value

  override def getType: String = TLangLong.getType

  override def compareTo(value: Value[Long]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue.toString

}

object TLangLong extends TLangType {
  override def getType: String = "Long"
}
