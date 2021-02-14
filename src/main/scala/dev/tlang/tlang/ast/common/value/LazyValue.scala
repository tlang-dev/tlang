package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.interpreter.Value

case class LazyValue[T <: Value[T]](var value: Option[T], valueType: TLangType) extends SimpleValueStatement[T] {
  override def getValue: T = value.get

  override def getType: String = valueType.getType

  override def compareTo(value: Value[T]): Int = this.value.get.compareTo(value)
}
