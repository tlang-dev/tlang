package dev.tlang.tlang.ast.common

import tlang.core.Type
import tlang.internal
import tlang.internal.Context

abstract class ValueType extends Context {
  def getContextType: Type

  def getType: Type

  override def toString: String = getContextType.getType.toString
}
