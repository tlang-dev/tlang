package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class EndStaticBox(id: String) extends Instruction with EndSeq {

  private var isInit = false

  override def run(state: State): Either[ExecError, Unit] = {
    if (!isInit) {
      state.removeBox()
      isInit = true
    }
    state.levels.staticBox -= 1
    Right(())
  }
}
