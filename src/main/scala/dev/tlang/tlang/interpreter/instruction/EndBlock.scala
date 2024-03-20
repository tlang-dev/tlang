package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class EndBlock() extends Instruction with ExecJump with EndSeq {
  override def run(state: State): Either[ExecError, Unit] = {
//    state.levels.block -= 1
    Right(())
  }
}
