package dev.tlang.tlang.ast.common

import tlang.internal

abstract class ValueType extends internal.AstContext {
  def getContextType: String

  def getType: String

  override def toString: String = getContextType
}
