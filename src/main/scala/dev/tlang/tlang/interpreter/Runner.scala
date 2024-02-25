package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.instruction.{EndSeq, ExecJump}
import dev.tlang.tlang.interpreter.recipe.Parameter

class Runner {

  private var sectionPos: Int = 0
  private var instrPos: Int = 0
  private var error: Option[ExecError] = None
  private val state = new State

  def run(program: Program, parameter: Parameter): Either[ExecError, Unit] = {
    sectionPos = parameter.sectionStart
    instrPos = parameter.instrStart
    val logger = parameter.logger
    state.setLogger(logger)
    do {
      val section = program.getSection(sectionPos)
      val instr = section.getInstr(instrPos)
      logger.debug("[" + instr.getClass.getSimpleName + "] Section n°: " + sectionPos + ", Instruction n°: " + instrPos)
      instr.run(state) match {
        case Left(err) => error = Some(err)
        case Right(_) =>
          if (instr.isInstanceOf[ExecJump] && (state.hasJump || state.hasGoto)) {
            val jump =
              if (state.hasGoto) program.getLabel(state.getGoTo)
              else state.getJump
            sectionPos = jump.section
            instrPos = jump.instruction
            logger.debug("Jumping to: " + sectionPos + ":" + instrPos)
          } else instrPos += 1
      }
    } while (
      error.isEmpty
        && (state.hasJump || !program.getSection(sectionPos).getInstr(instrPos - 1).isInstanceOf[EndSeq])
        && instrPos < program.getSection(sectionPos).getInstructions.length
    )
    if (error.isDefined) Left(error.get)
    else Right(())
  }

}
