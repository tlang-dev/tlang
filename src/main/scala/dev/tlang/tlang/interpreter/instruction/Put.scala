package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class Put(popFromBox: Boolean = false, pos: Int = 0) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    if (popFromBox) state.getStack.push(state.getBox.getRemoveAt(pos))
    else state.getStack.push(state.getBox.getAt(pos))
    Right(())
  }
}
