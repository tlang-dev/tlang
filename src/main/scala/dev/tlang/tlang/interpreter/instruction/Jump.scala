package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{JumpIndex, State}

case class Jump(pos: JumpIndex) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    state.jumpTo(pos)
    state.getLogger.debug("Set jump to: " + pos.section.toString + ":" + pos.instruction.toString)
    Right(())
  }
}