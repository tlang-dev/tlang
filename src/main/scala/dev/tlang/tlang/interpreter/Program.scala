package dev.tlang.tlang.interpreter

import scala.collection.mutable.ListBuffer

class Program {

  private val sections = ListBuffer.empty[Section]

  def addSection(section: Section): Unit = {
    sections.addOne(section)
  }

  def getSections: List[Section] = {
    sections.toList
  }

  def getSection(index: Int): Section = {
    sections(index)
  }

}
