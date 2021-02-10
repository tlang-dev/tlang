package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.helper.ConditionType.Value

object ForType extends Enumeration {
  type forType = Value
  val IN, TO, UNTIL = Value
}
