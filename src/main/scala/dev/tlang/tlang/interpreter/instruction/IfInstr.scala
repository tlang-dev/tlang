package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{JumpIndex, State}
import tlang.core.Bool

case class IfInstr(jumpTrue: JumpIndex, var jumpFalse: Option[JumpIndex]) extends Instruction with ExecJump {
  override def run(state: State): Either[ExecError, Unit] = {
    if (state.getStack.pop().asInstanceOf[Bool].get()) {
      println("It's true")
      state.jumpTo(jumpTrue)
    } else if (jumpFalse.isDefined) {
      println("It's false")
      state.jumpTo(jumpFalse.get)
    }
    Right(())
  }
}
