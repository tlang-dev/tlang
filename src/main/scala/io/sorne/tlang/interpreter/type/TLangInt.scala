package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class TLangInt(value: Int) extends Value[Int] {
  override def getValue: Int = value

  override def getType: String = TLangInt.getType

  override def compareTo(value: Value[Int]): Int = this.value.compareTo(value.getValue)
}

object TLangInt extends TLangType {
  override def getType: String = "Int"
}
