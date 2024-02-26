package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.{Null, Type, Value}
import tlang.internal.{ClassType, ContextContent}

case class LazyValue[T <: Value](context: Null, var value: Option[T], valueType: Option[TLangType]) extends ComplexValueStatement[T] {

//  override def getType: Type = if (valueType.isDefined) valueType.get.getType else ClassType.of(this.getClass)

}
