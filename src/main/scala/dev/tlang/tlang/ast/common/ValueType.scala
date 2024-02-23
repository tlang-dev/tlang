package dev.tlang.tlang.ast.common

import tlang.core.Type
import tlang.internal

abstract class ValueType extends internal.AstContext {
  def getContextType: Type

  def getType: Type

  override def toString: String = getContextType.getType.toString
}
