package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class EndLabel(name: String) extends Instruction with GotoBack with EndSeq {
  override def run(state: State): Either[ExecError, Unit] = {
    state.getLogger.debug("End of label:" + name)
//    state.levels.label -= 1
    Right(())
  }
}
