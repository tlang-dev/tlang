package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.Type

trait TLangType {

  def getType: Type

  def getValueType: ValueType

}
