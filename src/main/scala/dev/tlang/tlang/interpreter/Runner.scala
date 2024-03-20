package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.instruction.{EndLabel, ExecJump, GotoBack, Instruction}
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
    val startPos = startPoint(program, parameter, state)
    sectionPos = startPos._1
    instrPos = startPos._2
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
            case _: GotoBack if state.hasJumpBack && wasLastInstruction(state, instr) =>
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
    instr match {
      case label: EndLabel if state.runTil.nonEmpty && state.runTil.head == label.name =>
        state.runTil.pop()
        true
      case _ => false
    }
  }

  private def initStatic(program: Program, parameter: Parameter, state: State): Either[ExecError, Unit] = {
    program.getSections.foreach { static =>
      static.getStatics.foreach { index =>
        runProgram(program, Parameter(index.section, index.instruction, None, parameter.logger), state)
      }
    }
    Right(())
  }

  private def startPoint(program: Program, parameter: Parameter, state: State): (Int, Int) = {
    if (parameter.startLabel.isDefined) {
      val startJump = program.getLabel(parameter.startLabel.get)
      state.runTil += parameter.startLabel.get
      (startJump.section, startJump.instruction)
    } else (parameter.sectionStart, parameter.instrStart)
  }

}
