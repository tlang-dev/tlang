package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class EmbeddedValue(context: Option[ContextContent], value: Value) extends ComplexValueStatement[Value] {

}
