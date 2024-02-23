package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.instruction.Instruction

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Program {

  private val instructions = ListBuffer.empty[Instruction]

  def addInstruction(instruction: Instruction): Unit = {
    instructions.addOne(instruction)
  }

  def getInstructions: List[Instruction] = {
    instructions.toList
  }

  def getInstr(index: Int): Instruction = {
    instructions(index)
  }

}
