package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class EndLabel(name: String) extends Instruction with GotoBack {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getLogger.debug("End of label:" + name)
    Right(())
  }
}
