package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.Value

class TLangBool(value: Boolean) extends PrimitiveValue[Boolean]() {
  override def getValue: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)

  override def toString: String = if(getValue) "true" else "false"
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"
}
