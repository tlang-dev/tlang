package dev.tlang.tlang.ast.helper

object ConditionType extends Enumeration {
  type condition = Value
  val EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_EQUAL, NOT_EQUAL = Value
}
