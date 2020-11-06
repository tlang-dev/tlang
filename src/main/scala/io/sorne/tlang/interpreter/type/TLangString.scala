package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

class TLangString(value: String) extends Value[String] {
  override def getValue: String = value

  override def getType: String = TLangString.getType

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getValue)
}

object TLangString extends TLangType {
  override def getType: String = "String"
}
