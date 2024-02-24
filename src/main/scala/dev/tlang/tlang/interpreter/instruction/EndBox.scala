package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State

case class EndBox() extends Instruction with ExecJump with EndSeq {
  override def run(state: State): Either[ExecError, Unit] = {
    println("End box")
    state.removeBox()
    Right(())
  }
}
