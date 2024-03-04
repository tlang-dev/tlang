package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.value.InterValue
import dev.tlang.tlang.interpreter.{Program, Section}
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable

case class BuilderContext(
                           labels: mutable.Map[String, JumpIndex] = mutable.Map(),
                           program: Program = new Program(),
                           section: Section = new Section(),
                           callables: mutable.Map[String, InterValue] = mutable.Map(),
                           module: Module,
                           resource: Resource
                         ) {
  def instrPos: Int = section.getInstructions.size - 1

  def sectionPos: Int = program.getSections.size - 1
}
