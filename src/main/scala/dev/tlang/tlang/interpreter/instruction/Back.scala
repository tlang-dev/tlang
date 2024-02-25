package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{JumpIndex, State}

case class Back(pos: JumpIndex) extends Instruction with ExecJump {

  override def run(state: State): Either[ExecError, Unit] = {
    state.jumpTo(pos)
    Right(())
  }
}