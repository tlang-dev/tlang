package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.interpreter.{Box, Stack}

import scala.collection.mutable

class State {

  private val jumps = mutable.Stack[LabelIndex]()

  private val boxes = mutable.Stack[Box]()

  private val stack = new Stack

  def jumpTo(index: LabelIndex): Unit = {
    jumps.prepend(index)
  }

  def getJump: LabelIndex = {
    jumps.pop()
  }

  def hasJump: Boolean = {
    jumps.nonEmpty
  }

  def getBox: Box = {
    boxes.head
  }

  def newBox(): Unit = {
    boxes.prepend(new Box)
  }

  def removeBox(): Unit = {
    boxes.pop()
  }

  def getStack: Stack = {
    stack
  }

}
