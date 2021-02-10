package dev.tlang.tlang.ast.common.value

import io.sorne.tlang.interpreter.Value

class TLangString(value: String) extends PrimitiveValue[String] {
  override def getValue: String = value

  override def getType: String = TLangString.getType

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue

}

object TLangString extends TLangType {
  override def getType: String = "String"
}
