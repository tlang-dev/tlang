package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.instruction.{EndSeq, ExecJump}

class Runner {

  private var pos: Int = 0
  private var error: Option[ExecError] = None
  private val state = new State

  def run(program: Program, start: Int): Either[ExecError, Unit] = {
    pos = start
    do {
      val instr = program.getInstr(pos)
      instr.run(state) match {
        case Left(err) => error = Some(err)
        case Right(_) => {
          if (instr.isInstanceOf[ExecJump] && state.hasJump) {
            pos = state.getJump
          } else pos += 1
        }
      }

    } while (error.isEmpty && (state.hasJump || !program.getInstr(pos - 1).isInstanceOf[EndSeq]) && pos < program.getInstructions.length)
    if (error.isDefined) Left(error.get)
    else Right(())
  }

}
