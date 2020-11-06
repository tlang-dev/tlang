package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class TLangInt(value: TLangInt) extends Value[TLangInt] {
  override def getValue: TLangInt = value

  override def getType: String = TLangInt.getType

  override def compareTo(value: Value[TLangInt]): Int = this.value.compareTo(value.getValue)
}

object TLangInt extends TLangType {
  override def getType: String = "Int"
}
