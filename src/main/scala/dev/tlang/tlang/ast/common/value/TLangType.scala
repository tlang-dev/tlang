package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType

trait TLangType {

  def getType: String

  def getValueType: ValueType

}
