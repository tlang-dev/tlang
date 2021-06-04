package dev.tlang.tlang.ast.common

import dev.tlang.tlang.astbuilder.context.AstContext

abstract class ValueType extends AstContext {
  def getContextType: String

  def getType: String

  override def toString: String = getContextType
}
