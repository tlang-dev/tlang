package dev.tlang.tlang.interpreter

import dev.tlang.tlang.resolver.Element
import tlang.core

trait Value[T] extends Element[T] with core.Value[T] {

  def compareTo(value: Value[T]): Int

}
