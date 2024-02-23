package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class Set() extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getBox().set(state.getStack.load())
    Right(())
  }
}
