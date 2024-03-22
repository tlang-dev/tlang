package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction.Instruction

import scala.collection.mutable.ListBuffer

class Section {
  private val instructions = ListBuffer.empty[InstructionBlock]
  private val statics = ListBuffer.empty[JumpIndex]
  private var finalInstructions = List.empty[Instruction]
  private var currentInstructionBlock: Option[InstructionBlock] = None

  def newInstructionBlock(label: String): InstructionBlock = {
    val instruction = new InstructionBlock(label)
    instructions.addOne(instruction)
    currentInstructionBlock = Some(instruction)
    instruction
  }

//  def addInstruction(instruction: Instruction): Unit = {
//    currentInstructionBlock.foreach(_.addInstruction(instruction))
//  }

  def getCurrentInstructionBlock: Option[InstructionBlock] = currentInstructionBlock

  def getInstructions: List[Instruction] = finalInstructions

  def build: List[Instruction] = {
    finalInstructions = instructions.flatMap(_.build).toList
    finalInstructions
  }

  def getInstr(index: Int): Instruction = {
    finalInstructions(index)
  }

  def addStaticLabel(index: JumpIndex): Unit = {
    statics.addOne(index)
  }

  def getStatics: List[JumpIndex] = {
    statics.toList
  }
}
