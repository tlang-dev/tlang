package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

case class String(value: Predef.String) extends Value[Predef.String] {
  override def getValue: Predef.String = value

  override def getType: Predef.String = getClass.getName

  override def compareTo(value: Value[Predef.String]): Int = this.value.compareTo(value.getValue)
}
