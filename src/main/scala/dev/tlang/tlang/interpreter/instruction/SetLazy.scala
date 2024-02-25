package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Lazy

case class SetLazy(pos: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getBox.getAt(pos)
    if (value.isInstanceOf[Lazy]) {
      state.getBox.replaceAt(pos, state.getStack.pop())
    }
    Right(())
  }
}
