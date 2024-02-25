package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.{Box, ExecError, Program, Stack}

case class Delete() extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getStack.delete()
    Right(())
  }
}