package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class Back(pos: Int) extends Instruction with ExecJump {

  override def run(state: State): Either[ExecError, Unit] = {
    state.jumpTo(pos)
    Right(())
  }
}
