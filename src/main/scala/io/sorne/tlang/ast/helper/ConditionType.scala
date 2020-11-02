package io.sorne.tlang.ast.helper

object ConditionType extends Enumeration {
  type condition = Value
  val EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_SMALLER, NOT_EQUAL = Value
}
