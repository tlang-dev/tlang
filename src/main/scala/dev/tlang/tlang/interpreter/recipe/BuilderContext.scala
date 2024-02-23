package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.interpreter.Program

import scala.collection.mutable

case class BuilderContext(
                           labels: mutable.Map[String, Int] = mutable.Map(),
                           program: Program = new Program(),
                         ) {
  def pos: Int = program.getInstructions.size - 1
}
