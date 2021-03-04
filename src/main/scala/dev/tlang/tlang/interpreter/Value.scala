package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.context.AstContext

trait Value[T] extends AstContext {

  def getValue: T

  def getType: String

  def compareTo(value: Value[T]): Int

}
