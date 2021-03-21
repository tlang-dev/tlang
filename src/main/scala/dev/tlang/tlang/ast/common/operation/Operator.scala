package dev.tlang.tlang.ast.common.operation

object Operator extends Enumeration {
  type operator = Value
  val OR, AND, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
  EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_EQUAL, NOT_EQUAL = Value
}
