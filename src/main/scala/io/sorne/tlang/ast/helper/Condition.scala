package io.sorne.tlang.ast.helper

object Condition extends Enumeration {
  type condition = Value
  val Equals, GreaterThan, LesserThan, GreaterOrEqualThan, LesserOrSmallerThan, NotEquals = Value
}
