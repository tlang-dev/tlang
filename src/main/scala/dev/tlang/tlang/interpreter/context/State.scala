package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.interpreter.recipe.{DefaultLogger, Logger}
import dev.tlang.tlang.interpreter.{Box, Stack}

import scala.collection.mutable

class State {

  private val jumps = mutable.Stack[JumpIndex]()

  private val jumpBacks = mutable.Stack[JumpIndex]()

  private val gotos = mutable.Stack[String]()

  private val boxes = mutable.Stack[Box]()

  private val staticBoxes = mutable.Map[String, Box]()

  private val stack = new Stack

  private var logger: Logger = new DefaultLogger

  def jumpTo(index: JumpIndex): Unit = {
    jumps.prepend(index)
  }

  def getJump: JumpIndex = {
    jumps.pop()
  }

  def jumpBackTo(index: JumpIndex): Unit = {
    jumpBacks.prepend(index)
  }

  def getJumpBack: JumpIndex = {
    jumpBacks.pop()
  }

  def goto(label: String): Unit = {
    gotos.prepend(label)
  }

  def getGoTo: String = {
    gotos.pop()
  }

  def hasJump: Boolean = {
    jumps.nonEmpty
  }

  def hasJumpBack: Boolean = {
    jumpBacks.nonEmpty
  }

  def hasGoto: Boolean = {
    gotos.nonEmpty
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

  def newStaticBox(name: String): Unit = {
    staticBoxes += (name -> new Box)
  }

  def getStaticBox(id: String): Box = {
    staticBoxes(id)
  }

  def setLogger(logger: Logger): Unit = {
    this.logger = logger
  }

  def getLogger: Logger = {
    logger
  }

}
