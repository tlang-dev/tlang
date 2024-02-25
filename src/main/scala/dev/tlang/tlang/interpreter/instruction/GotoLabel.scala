package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class GotoLabel(label: String) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.goto(label)
    state.getLogger.debug("Set Goto to: " + label)
    Right(())
  }
}
