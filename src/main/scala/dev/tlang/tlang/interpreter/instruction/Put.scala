package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class Put() extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getStack.push(state.getBox().get())
    Right(())
  }
}
