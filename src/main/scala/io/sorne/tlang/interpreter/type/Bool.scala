package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.ast.model.`new`.ModelNewValueType
import io.sorne.tlang.interpreter.Value

class Bool(value: Boolean) extends ModelNewValueType[Boolean]() {
  override def getValue: Boolean = value

  override def getType: Predef.String = getClass.getName

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)
}
