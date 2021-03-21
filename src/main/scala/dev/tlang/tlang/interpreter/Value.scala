package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.context.AstContext
import dev.tlang.tlang.resolver.Element

trait Value[T] extends AstContext with Element[T] {

  def compareTo(value: Value[T]): Int

}
