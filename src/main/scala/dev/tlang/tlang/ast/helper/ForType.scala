package dev.tlang.tlang.ast.helper

import io.sorne.tlang.ast.helper.ConditionType.Value

object ForType extends Enumeration {
  type forType = Value
  val IN, TO, UNTIL = Value
}
