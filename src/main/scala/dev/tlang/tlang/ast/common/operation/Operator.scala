package dev.tlang.tlang.ast.common.operation

object Operator extends Enumeration {
  type operator = Value
  val ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
  OR, AND, EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_EQUAL, NOT_EQUAL = Value

//  val conditionals = List(OR, AND, EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_EQUAL, NOT_EQUAL)
//  val mathematical = List(ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO)

  val priorities = List(
    List(MULTIPLY, DIVIDE, MODULO),
    List(ADD, SUBTRACT),
    List(EQUAL, GREATER, LESSER, GREATER_OR_EQUAL, LESSER_OR_EQUAL, NOT_EQUAL),
    List(OR, AND)
  )
}
