package io.sorne.tlang.interpreter.`type`

import io.sorne.tlang.interpreter.Value

case class String(value: String) extends Value[String] {
  override def getValue: String = value

  override def getType: Predef.String = getClass.getName

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getValue)
}
