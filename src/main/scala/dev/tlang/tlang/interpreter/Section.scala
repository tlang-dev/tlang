package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction.Instruction

import scala.collection.mutable.ListBuffer

class Section {
  private val instructions = ListBuffer.empty[Instruction]
  private val statics = ListBuffer.empty[JumpIndex]

  def addInstruction(instruction: Instruction): Unit = {
    instructions.addOne(instruction)
  }

  def getInstructions: List[Instruction] = {
    instructions.toList
  }

  def getInstr(index: Int): Instruction = {
    instructions(index)
  }

  def addStaticLabel(index: JumpIndex): Unit = {
    statics.addOne(index)
  }

  def getStatics: List[JumpIndex] = {
    statics.toList
  }
}
