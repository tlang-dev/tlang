package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class EmbeddedValue(context: Null, value: Value) extends ComplexValueStatement[Value] {

}
