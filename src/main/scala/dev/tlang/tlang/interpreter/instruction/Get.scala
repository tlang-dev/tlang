package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class Get(pos: Int = 0) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getStack.push(state.getBox.getAt(pos))
    Right(())
  }
}
