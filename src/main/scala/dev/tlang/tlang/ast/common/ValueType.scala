package dev.tlang.tlang.ast.common

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal
import tlang.internal.Context

abstract class ValueType extends AstContext {
  def getContextType: Type

  def getType: Type

  override def toString: String = getContextType.getType.toString
}
