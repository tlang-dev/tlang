package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.instruction.{EndSeq, ExecJump, GotoBack, Instruction}
import dev.tlang.tlang.interpreter.recipe.Parameter
import tlang.core.Value

class Runner {

  private var sectionPos: Int = 0
  private var instrPos: Int = 0
  private var error: Option[ExecError] = None

  def initAndRun(state: State, parameter: Parameter): Either[ExecError, Option[Value]] = {
    initStatic(state.program, parameter, state)
    runProgram(state.program, parameter, state)
  }

  def run(state: State, parameter: Parameter): Either[ExecError, Option[Value]] = {
    runProgram(state.program, parameter, state)
  }

  private def runProgram(program: Program, parameter: Parameter, state: State): Either[ExecError, Option[Value]] = {
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
          instr match {
            case _: ExecJump if state.hasJump || state.hasGoto =>
              val jump =
                if (state.hasGoto) program.getLabel(state.getGoTo)
                else state.getJump
              sectionPos = jump.section
              instrPos = jump.instruction
              logger.debug("Jumping to: " + sectionPos + ":" + instrPos)
            case _: GotoBack if state.hasJumpBack =>
              val jump = state.getJumpBack
              sectionPos = jump.section
              instrPos = jump.instruction
              logger.debug("Jumping back to: " + sectionPos + ":" + instrPos)
            case _ => instrPos += 1
          }
      }
    } while (
      error.isEmpty
        && (state.hasJump || state.hasJumpBack || !wasLastInstruction(state, program.getSection(sectionPos).getInstr(instrPos - 1)))
        && instrPos < program.getSection(sectionPos).getInstructions.length
    )
    if (error.isDefined) Left(error.get)
    else if (state.stack.isNonEmpty) Right(Some(state.stack.pop()))
    else Right(None)
  }

  private def wasLastInstruction(state: State, instr: Instruction): Boolean = {
    instr.isInstanceOf[EndSeq] && state.levels.label == 0 && state.levels.box == 0
  }

  private def initStatic(program: Program, parameter: Parameter, state: State): Either[ExecError, Unit] = {
    program.getSections.foreach { static =>
      static.getStatics.foreach { index =>
        runProgram(program, Parameter(index.section, index.instruction, parameter.logger), state)
      }
    }
    Right(())
  }

}
