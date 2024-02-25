package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.JumpIndex

import scala.collection.mutable.ListBuffer

class Program {

  private val sections = ListBuffer.empty[Section]
  private var labels = Map[String, JumpIndex]()

  def addSection(section: Section): Unit = {
    sections.addOne(section)
  }

  def getSections: List[Section] = {
    sections.toList
  }

  def getSection(index: Int): Section = {
    sections(index)
  }

  def setLabels(labels: Map[String, JumpIndex]): Unit = {
    this.labels = labels
  }

  def getLabel(name: String): JumpIndex = {
    labels(name)
  }

}
