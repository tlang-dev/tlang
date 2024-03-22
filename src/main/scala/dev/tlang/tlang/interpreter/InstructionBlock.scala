package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.instruction.{EndLabel, Instruction, Label}

import scala.collection.mutable.ListBuffer

class InstructionBlock(labelName: String) {

  private val instructions = ListBuffer.empty[Instruction]
  private var finalInstructions = List.empty[Instruction]

  def addInstruction(instruction: Instruction): Unit = instructions += instruction

  def getInstructions: List[Instruction] = finalInstructions

  def build: List[Instruction] = {
    instructions.prepend(Label(labelName))
    instructions += EndLabel(labelName)
    finalInstructions = instructions.toList
    finalInstructions
  }
}
