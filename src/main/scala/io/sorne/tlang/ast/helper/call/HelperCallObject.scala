package io.sorne.tlang.ast.helper.call

import io.sorne.tlang.interpreter.Value
import io.sorne.tlang.interpreter.`type`.TLangType

case class HelperCallObject(statements: List[HelperCallObjectType]) extends HelperAttributeStatement {
  override def getValue: HelperAttributeStatement = this

  override def getType: String = "HelperCallObject"

  override def compareTo(value: Value[HelperAttributeStatement]): Int = 0
}
