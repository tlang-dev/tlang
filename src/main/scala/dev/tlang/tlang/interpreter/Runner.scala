package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.instruction.{EndSeq, ExecJump}

class Runner {

  private var sectionPos: Int = 0
  private var instrPos: Int = 0
  private var error: Option[ExecError] = None
  private val state = new State

  def run(program: Program, sectionStart: Int, instrStart: Int): Either[ExecError, Unit] = {
    sectionPos = sectionStart
    instrPos = instrStart
    do {
      println("Section n°: " + sectionPos + ", Instruction n°: " + instrPos)
      val section = program.getSection(sectionPos)
      val instr = section.getInstr(instrPos)
      instr.run(state) match {
        case Left(err) => error = Some(err)
        case Right(_) => {
          if (instr.isInstanceOf[ExecJump] && state.hasJump) {
            val jump = state.getJump
            sectionPos = jump.section
            instrPos = jump.instruction
            println("Jump to: " + sectionPos + ":" + instrPos)
          } else instrPos += 1
        }
      }

    } while (error.isEmpty && (state.hasJump || !program.getSection(sectionPos).getInstr(instrPos - 1).isInstanceOf[EndSeq]) && instrPos < program.getSection(sectionPos).getInstructions.length)
    if (error.isDefined) Left(error.get)
    else Right(())
  }

}
