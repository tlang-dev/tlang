package io.sorne.tlang.ast.common.value

import io.sorne.tlang.interpreter.Value

class TLangBool(value: Boolean) extends PrimitiveValue[Boolean]() {
  override def getValue: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"
}
