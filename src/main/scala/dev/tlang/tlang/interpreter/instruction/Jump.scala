package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{LabelIndex, State}

case class Jump(pos:LabelIndex) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.jumpTo(pos)
    Right(())
  }
}