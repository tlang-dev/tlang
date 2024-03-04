package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class EndStaticBox(id: String) extends Instruction with EndSeq {

  override def run(state: State): Either[ExecError, Unit] = {
    state.removeBox()
    Right(())
  }
}
