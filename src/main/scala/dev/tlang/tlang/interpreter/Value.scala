package dev.tlang.tlang.interpreter

import dev.tlang.tlang.resolver.Element

trait Value[T] extends Element[T] {

  def compareTo(value: Value[T]): Int

}
