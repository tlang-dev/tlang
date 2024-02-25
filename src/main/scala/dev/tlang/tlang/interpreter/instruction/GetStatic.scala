package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class GetStatic(id: String) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getStack.push(state.getStaticBox(id).get())
    Right(())
  }
}
