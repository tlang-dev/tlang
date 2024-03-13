package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.interpreter.recipe.{DefaultLogger, Logger}
import dev.tlang.tlang.interpreter.{Box, Program, Stack}

import scala.collection.mutable

case class State(jumps: mutable.Stack[JumpIndex] = mutable.Stack.empty,
                 jumpBacks: mutable.Stack[JumpIndex] = mutable.Stack.empty,
                 gotos: mutable.Stack[String] = mutable.Stack.empty,
                 boxes: mutable.Stack[Box] = mutable.Stack.empty,
                 staticBoxes: mutable.Map[String, Box] = mutable.Map.empty,
                 stack: Stack = new Stack,
                 program: Program,
                 var logger: Logger = new DefaultLogger,
                 levels: Levels = Levels()) {

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
