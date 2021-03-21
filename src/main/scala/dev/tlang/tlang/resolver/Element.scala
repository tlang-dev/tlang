package dev.tlang.tlang.resolver

import dev.tlang.tlang.astbuilder.context.AstContext

trait Element[T] extends AstContext {

  def getElement: T

  def getType: String

}
