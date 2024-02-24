package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.interpreter.context.LabelIndex
import dev.tlang.tlang.interpreter.{Program, Section}
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable

case class BuilderContext(
                           labels: mutable.Map[String, LabelIndex] = mutable.Map(),
                           program: Program = new Program(),
                           section: Section = new Section(),
                           module: Module,
                           resource: Resource
                         ) {
  def instrPos: Int = section.getInstructions.size - 1

  def sectionPos:Int = program.getSections.size - 1
}
