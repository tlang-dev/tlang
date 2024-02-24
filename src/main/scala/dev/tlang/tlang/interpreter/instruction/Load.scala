package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class Load(popFromStack: Boolean = false) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    if (popFromStack) state.getBox.set(state.getStack.pop())
    else state.getBox.set(state.getStack.load())
    Right(())
  }
}